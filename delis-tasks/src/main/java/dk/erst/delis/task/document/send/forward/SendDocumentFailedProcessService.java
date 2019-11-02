package dk.erst.delis.task.document.send.forward;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.JournalSendDocumentDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentProcessStepType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.document.send.SendDocumentLockService;
import dk.erst.delis.task.document.storage.SendDocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.vfs.service.VFSService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendDocumentFailedProcessService {

	private SendDocumentLockService sendDocumentLockService;
	private OrganisationSetupService organisationSetupService;
	private SendDocumentBytesStorageService sendDocumentBytesStorageService;
	private JournalSendDocumentDaoRepository journalSendDocumentDaoRepository;
	private VFSService vfsService;
	private String forwardSetup;

	@Autowired
	public SendDocumentFailedProcessService(

			SendDocumentLockService sendDocumentLockService,

			OrganisationSetupService organisationSetupService,

			SendDocumentBytesStorageService sendDocumentBytesStorageService,

			JournalSendDocumentDaoRepository journalSendDocumentDaoRepository,

			VFSService vfsService,

			@Value("${delis.forward.failed.invoiceresponse.vfs.config.path:#{null}}") String forwardSetup

	) {
		this.sendDocumentLockService = sendDocumentLockService;
		this.organisationSetupService = organisationSetupService;
		this.sendDocumentBytesStorageService = sendDocumentBytesStorageService;
		this.journalSendDocumentDaoRepository = journalSendDocumentDaoRepository;
		this.vfsService = vfsService;
		this.forwardSetup = forwardSetup;
	}

	@Data
	private static class OrganisationForwardSetup {
		private boolean forward;
	}

	public StatData processFailedDocuments() {
		StatData statData = new StatData();
		SendDocument sendDocument;
		long start = System.currentTimeMillis();

		Map<Long, OrganisationForwardSetup> organisationIdToSetupMap = null;

		while ((sendDocument = sendDocumentLockService.findDocumentAndLock(SendDocumentStatus.SEND_ERROR, SendDocumentStatus.FORWARD_START)) != null) {
			if (organisationIdToSetupMap == null && forwardSetup != null) {
				organisationIdToSetupMap = loadOrganisationIdToSetupMap();
			}
			SendDocumentStatus status;
			try {
				status = processDocument(sendDocument, organisationIdToSetupMap);
			} catch (Exception e) {
				status = SendDocumentStatus.FORWARD_FAILED;
				log.error("Failed to process failed delivery of " + sendDocument, e);
			}
			statData.incrementObject(status);
		}
		
		long duration = System.currentTimeMillis() - start;
		if (duration > 500 || !statData.isEmpty()) {
			log.info("Done processing of failed documents during sending in " + duration + "ms : " + statData);
		}
		return statData;
	}

	protected Map<Long, OrganisationForwardSetup> loadOrganisationIdToSetupMap() {
		List<Long> organisationIdWithSetup = organisationSetupService.loadOrganisationIdWithSetup(OrganisationSetupKey.SEND_UNDELIVERABLE_RESPONSE_TO_ERST, "true");

		Map<Long, OrganisationForwardSetup> res = new HashMap<>();
		for (Long organisationId : organisationIdWithSetup) {
			OrganisationForwardSetup forwardSetup = new OrganisationForwardSetup();
			forwardSetup.setForward(true);
			res.put(organisationId, forwardSetup);
		}
		return res;

	}

	protected SendDocumentStatus processDocument(SendDocument sendDocument, Map<Long, OrganisationForwardSetup> organisationIdToSetupMap) {
		long start = System.currentTimeMillis();
		SendDocumentStatus resultStatus = SendDocumentStatus.FORWARD_SKIPPED;
		String outputFileName = sendDocument.getDocumentType().getCode() +"_" + StringUtils.leftPad(String.valueOf(sendDocument.getId()), 5, "0") + ".xml";
		String logMessage = "Failed delivery forwarding is not configured";
		try {
			OrganisationForwardSetup forwardSetup = null;
			if (organisationIdToSetupMap != null && sendDocument.getOrganisation() != null) {
				forwardSetup = organisationIdToSetupMap.get(sendDocument.getOrganisation().getId());
				if (forwardSetup != null && forwardSetup.forward) {
					SendDocumentBytes original = sendDocumentBytesStorageService.find(sendDocument, SendDocumentBytesType.ORIGINAL);
					boolean ok = false;
					try {
						ok = moveToVFS(original, outputFileName, this.forwardSetup);
					} catch (Exception e) {
						log.error("Failed moveToVFS for " + outputFileName + " with setup " + this.forwardSetup, e);
						resultStatus = SendDocumentStatus.FORWARD_FAILED;
						logMessage = "Failed to forward " + outputFileName + " with error " + e.getMessage();
						return resultStatus;
					}
					if (ok) {
						resultStatus = SendDocumentStatus.FORWARD_OK;
						logMessage = "Successfully forwarded document as " + outputFileName;
					} else {
						resultStatus = SendDocumentStatus.FORWARD_FAILED;
						logMessage = "Failed to forward document by VFS config " + this.forwardSetup;
					}
				}
			}
		} finally {
			saveJournal(sendDocument, resultStatus != SendDocumentStatus.FORWARD_FAILED, logMessage, SendDocumentProcessStepType.FORWARD, System.currentTimeMillis() - start);
			sendDocumentLockService.unlock(sendDocument, SendDocumentStatus.FORWARD_START, resultStatus);
		}
		return resultStatus;
	}

	protected void saveJournal(SendDocument sd, boolean success, String logMessage, SendDocumentProcessStepType stepType, long durationMs) {
		JournalSendDocument journalRecord = new JournalSendDocument();
		journalRecord.setSuccess(success);
		journalRecord.setDocument(sd);
		journalRecord.setType(stepType);
		journalRecord.setOrganisation(sd.getOrganisation());
		journalRecord.setMessage(StringUtils.truncate(logMessage, 250));
		journalRecord.setDurationMs(durationMs);
		journalSendDocumentDaoRepository.save(journalRecord);
	}

	protected boolean moveToVFS(SendDocumentBytes documentBytes, String outputFileName, String configPath) throws Exception {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("delis", "tmp");
		} catch (IOException e) {
			log.error("Unable to create temp file", e);
		}
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			boolean loaded = sendDocumentBytesStorageService.load(documentBytes, fileOutputStream);
			if (loaded) {
				vfsService.upload(configPath, tempFile.getAbsolutePath(), "/" + outputFileName);
				return true;
			}
		} finally {
			if (tempFile != null && !tempFile.delete()) {
				log.warn(String.format("Unable to delete temp file '%s'", tempFile.getAbsolutePath()));
			}
		}
		return false;
	}
}
