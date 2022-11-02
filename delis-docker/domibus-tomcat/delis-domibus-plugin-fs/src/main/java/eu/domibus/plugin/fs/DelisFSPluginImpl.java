package eu.domibus.plugin.fs;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.domibus.common.DeliverMessageEvent;
import eu.domibus.ext.domain.DomainDTO;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import eu.domibus.logging.DomibusMessageCode;
import eu.domibus.logging.MDCKey;
import eu.domibus.messaging.MessageConstants;
import eu.domibus.messaging.MessageNotFoundException;
import eu.domibus.plugin.fs.exception.FSPluginException;
import eu.domibus.plugin.fs.exception.FSSetUpException;
import eu.domibus.plugin.fs.worker.FSSendMessagesService;

public class DelisFSPluginImpl extends FSPluginImpl {

	private static final String FSPLUGIN_MESSAGES_LOCATION_IN_ONE_FOLDER_INCLUDE_RECIPIENT = "fsplugin.messages.location.in.oneFolderIncludeRecipient";
	private static final String FSPLUGIN_MESSAGES_LOCATION_IN_ONE_FOLDER = "fsplugin.messages.location.in.oneFolder";
	
	private static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(DelisFSPluginImpl.class);
	
    @Override
    @MDCKey({DomibusLogger.MDC_MESSAGE_ID, DomibusLogger.MDC_MESSAGE_ENTITY_ID})
    public void deliverMessage(DeliverMessageEvent event) {
        String fsPluginDomain = fsDomainService.getFSPluginDomain();
        if (!fsPluginProperties.getDomainEnabled(fsPluginDomain)) {
            LOG.error("Domain [{}] is disabled for FSPlugin", fsPluginDomain);
            return;
        }

        LOG.debug("Using FS Plugin domain [{}]", fsPluginDomain);
        String messageId = event.getMessageId();
        LOG.debug("Delivering File System Message [{}] to [{}]", messageId, event.getProps().get(MessageConstants.FINAL_RECIPIENT));
        FSMessage fsMessage;

        // Browse message
        try {
            fsMessage = browseMessage(messageId, null);
        } catch (MessageNotFoundException e) {
            LOG.businessError(DomibusMessageCode.BUS_MESSAGE_RETRIEVE_FAILED, e);
            throw new FSPluginException("Unable to browse message " + messageId, e);
        }

        //extract final recipient
        final String finalRecipient = event.getProps().get(MessageConstants.FINAL_RECIPIENT);
        if (StringUtils.isBlank(finalRecipient)) {
            LOG.businessError(DomibusMessageCode.BUS_MESSAGE_RETRIEVE_FAILED);
            throw new FSPluginException("Unable to extract finalRecipient from message " + messageId);
        }
        
        // 
        boolean oneFolder = booleanPropertyBithDefault(false, FSPLUGIN_MESSAGES_LOCATION_IN_ONE_FOLDER);
        boolean includeRecipient = false;
        if (oneFolder) {
        	includeRecipient = booleanPropertyBithDefault(true, FSPLUGIN_MESSAGES_LOCATION_IN_ONE_FOLDER_INCLUDE_RECIPIENT);
        }

        final String finalRecipientFolder = sanitizeFileName(finalRecipient);
        final String messageIdFolder = (includeRecipient ? finalRecipientFolder + "-":"") + sanitizeFileName(messageId);


        // Persist message
        try (FileObject rootDir = fsFilesManager.setUpFileSystem(fsPluginDomain);
             FileObject incomingFolder = fsFilesManager.getEnsureChildFolder(rootDir, FSFilesManager.INCOMING_FOLDER);
             FileObject incomingFolderByRecipient = oneFolder ? null : fsFilesManager.getEnsureChildFolder(incomingFolder, finalRecipientFolder);
             FileObject incomingFolderByMessageId = fsFilesManager.getEnsureChildFolder(oneFolder ? incomingFolder : incomingFolderByRecipient, messageIdFolder)) {

            //let's write the metadata file first
            try (FileObject fileObject = incomingFolderByMessageId.resolveFile(FSSendMessagesService.METADATA_FILE_NAME);
                 FileContent fileContent = fileObject.getContent()) {

                writeMetadata(fileContent.getOutputStream(), fsMessage.getMetadata());
                LOG.info("Message metadata file written at: [{}]", fileObject.getName().getURI());
            }

            final boolean scheduleFSMessagePayloadsSaving = scheduleFSMessagePayloadsSaving(fsMessage);
            if (scheduleFSMessagePayloadsSaving) {
                LOG.debug("FSMessage payloads for message [{}] will be scheduled for saving", messageId);

                final DomainDTO domainDTO = fsDomainService.fsDomainToDomibusDomain(fsPluginDomain);
                final Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
                domainTaskExtExecutor.submitLongRunningTask(() -> {
                    SecurityContextHolder.getContext().setAuthentication(currentAuthentication);
                    writePayloads(messageId, fsMessage, incomingFolderByMessageId);
                }, domainDTO);
            } else {
                writePayloads(messageId, fsMessage, incomingFolderByMessageId);
            }
        } catch (JAXBException ex) {
            LOG.businessError(DomibusMessageCode.BUS_MESSAGE_RETRIEVE_FAILED, ex);
            throw new FSPluginException("An error occurred while writing metadata for downloaded message " + messageId, ex);
        } catch (IOException | FSSetUpException ex) {
            LOG.businessError(DomibusMessageCode.BUS_MESSAGE_RETRIEVE_FAILED, ex);
            throw new FSPluginException("An error occurred persisting downloaded message " + messageId, ex);
        }
    }

	private boolean booleanPropertyBithDefault(boolean defaultValue, String string) {
		Boolean b = fsPluginProperties.getKnownBooleanPropertyValue(string);
		return b == null ? defaultValue : b.booleanValue();
	}

}
