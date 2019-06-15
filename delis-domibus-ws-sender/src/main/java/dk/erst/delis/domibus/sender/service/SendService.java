package dk.erst.delis.domibus.sender.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import dk.erst.delis.document.sbdh.AlreadySBDHException;
import dk.erst.delis.document.sbdh.SBDHTranslator;
import dk.erst.delis.domibus.sender.ConfigProperties;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.plugin.webService.generated.BackendInterface;
import eu.domibus.plugin.webService.generated.BackendService11;
import eu.domibus.plugin.webService.generated.ErrorResultImpl;
import eu.domibus.plugin.webService.generated.ErrorResultImplArray;
import eu.domibus.plugin.webService.generated.GetErrorsRequest;
import eu.domibus.plugin.webService.generated.LargePayloadType;
import eu.domibus.plugin.webService.generated.MessageStatus;
import eu.domibus.plugin.webService.generated.StatusRequest;
import eu.domibus.plugin.webService.generated.SubmitRequest;
import eu.domibus.plugin.webService.generated.SubmitResponse;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;

@Service
@Slf4j
public class SendService {

	private static final String EBXML_MSG_NS = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/";
	public static final String STRING_TYPE = "string";
	public static final String TEXT_XML = "text/xml";

	private ConfigProperties config;

	public SendService(ConfigProperties configProperties) {
		config = configProperties;

		if (config.isWsDumpHttp()) {
			System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		}
	}

	public SendWSResponse send(File file) throws Exception {

		SendWSResponse r = new SendWSResponse();

		File sbdFile = null;
		try {
			Header header;
			try {
				SBDHTranslator sbdhTranslator = new SBDHTranslator();
				Path sbdPath = Files.createTempFile(file.getName(), ".xml");
				header = sbdhTranslator.addHeader(file.toPath(), sbdPath);
				sbdFile = sbdPath.toFile();
			} catch (AlreadySBDHException ae) {
				log.info("Uploaded file is already an SBDH - use it without enrichment");
				
				sbdFile = file;
				try (InputStream is = new FileInputStream(sbdFile)) {
					SbdReader reader = SbdReader.newInstance(is);
					header = reader.getHeader();
				} catch (Exception en) {
					log.error("Failed to parse file " + file + " as SBDH", en);
					r.addError("Failed to parse file as SBDH: "+en.getMessage());
					return r;
				}
			} catch (Exception e) {
				log.error("Failed to wrap file " + file + " with SBDH", e);
				r.addError("Failed to prepare SBDH by file: "+e.getMessage());
				return r;
			}

			String url = config.getWsdlUrl();

			log.info("Sending via " + url);
			BackendService11 client = new BackendService11(new URL(url), new QName("http://org.ecodex.backend/1_1/", "BackendService_1_1"));

			String payloadHref = "cid:message";
			SubmitRequest submitRequest = createSubmitRequest(payloadHref, sbdFile);
			Messaging ebMSHeaderInfo = createMessageHeader(header, config.getWsSendParty());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serialize(ebMSHeaderInfo.getUserMessage(), baos);

			if (log.isDebugEnabled()) {
				log.debug("Sending to " + url + " userMessage:\n" + new String(baos.toByteArray(), StandardCharsets.UTF_8));
			}

			BackendInterface backendport = client.getBACKENDPORT();

			if (config.isWsUseAuth()) {
				BindingProvider binding = ((BindingProvider) backendport);
				String login = config.getWsLogin();
				String password = config.getWsPassword();

				binding.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, login);
				binding.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

				byte[] base64CredsBytes = Base64.encodeBase64((login + ":" + password).getBytes());
				String base64Creds = new String(base64CredsBytes);
				binding.getRequestContext().put("Authorization", "Basic " + base64Creds);

				if (config.isWsForceHttps()) {
					log.info("Try to force HTTPS for endpoint address");
					String endpointAddressCurrent = (String) binding.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
					log.info("Current ENDPOINT_ADDRESS_PROPERTY=" + endpointAddressCurrent);
					if (endpointAddressCurrent != null) {
						if (endpointAddressCurrent.startsWith("https:")) {
							log.info("Endpoint address already uses https, do nothing");
						} else {
							String endpointAddressNew = endpointAddressCurrent.replace("http:", "https:");
							log.info("Replaced endpoint address with " + endpointAddressNew);
							binding.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddressNew);
						}
					}
				}
			}

			SubmitResponse response = backendport.submitMessage(submitRequest, ebMSHeaderInfo);

			List<String> messageID = response.getMessageID();
			log.info("Submitted message with messageId list (" + messageID.size() + " ) : " + messageID);

			StatusRequest statusRequest = new StatusRequest();
			String messageId = messageID.get(0);
			statusRequest.setMessageID(messageId);

			MessageStatus status = null;

			boolean statusChanged = false;

			long maxWait = System.currentTimeMillis() + config.getResultMaxWaitMs();
			while (!statusChanged && System.currentTimeMillis() < maxWait) {
				status = backendport.getStatus(statusRequest);
				log.info("Message " + messageId + " status: " + status);

				statusChanged = status != MessageStatus.SEND_ENQUEUED || status != MessageStatus.SEND_ATTEMPT_FAILED || status != MessageStatus.SEND_FAILURE;

				if (statusChanged) {
					log.info("Message " + messageId + " status changed");
					break;
				} else {
					log.info("Wait before next change for message " + messageId + " status change");
					Thread.sleep(config.getResultCheckIntervalMs());
				}
			}

			GetErrorsRequest errorsRequest = new GetErrorsRequest();
			errorsRequest.setMessageID(messageId);
			ErrorResultImplArray messageErrors = backendport.getMessageErrors(errorsRequest);

			r.setSuccess(true);
			r.setMessageId(messageId);
			r.setMessageStatus(status.toString());

			if (messageErrors != null && messageErrors.getItem() != null) {

				for (ErrorResultImpl errorItem : messageErrors.getItem()) {

					StringBuilder sb = new StringBuilder();
					sb.append("MessageInErrorId=");
					sb.append(errorItem.getMessageInErrorId());
					sb.append(", errorCode=");
					sb.append(errorItem.getErrorCode());
					sb.append(", detail=");
					sb.append(errorItem.getErrorDetail());

					r.addError(sb.toString());
				}
			}
		} finally {
			if (sbdFile != null && sbdFile.exists()) {
				if (!sbdFile.delete()) {
					log.warn("SBD file was not deleted, delete on exit: " + sbdFile);
					sbdFile.deleteOnExit();
				}
			}
		}
		return r;
	}

	private SubmitRequest createSubmitRequest(String payloadHref, File xml) {
		final SubmitRequest submitRequest = new SubmitRequest();
		LargePayloadType largePayload = new LargePayloadType();
		final DataHandler messageHandler = new DataHandler(new FileDataSource(xml));
		largePayload.setPayloadId(payloadHref);
		largePayload.setContentType(TEXT_XML);
		largePayload.setValue(messageHandler);
		submitRequest.getPayload().add(largePayload);
		return submitRequest;
	}

	protected Messaging createMessageHeader(Header sbdh, String partyCode) {
		WSStubUserMessageBuilder b = new WSStubUserMessageBuilder();
		UserMessage userMessage = b.buildUserMessage(sbdh, partyCode);
		Messaging ebMSHeaderInfo = new Messaging();
		ebMSHeaderInfo.setUserMessage(userMessage);
		return ebMSHeaderInfo;
	}

	private static void writeXML(OutputStream outputStream, Class<UserMessage> clazz, UserMessage objectToWrite) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		QName qName = new QName(EBXML_MSG_NS, clazz.getSimpleName());
		marshaller.marshal(new JAXBElement<UserMessage>(qName, clazz, (Class<?>) null, objectToWrite), outputStream);
	}

	public void serialize(UserMessage userMessage, OutputStream out) throws JAXBException {
		writeXML(out, UserMessage.class, userMessage);
	}
}
