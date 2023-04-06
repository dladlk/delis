package dk.erst.delis.sender.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.oxalis.sender.TransmissionException;
import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.document.IDocumentData;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;

@Service
@Slf4j
public class SendService {

	private SendContext sendContext;

	@Autowired
	public SendService(SendContext sendContext) {
		this.sendContext = sendContext;
	}
	
	public enum SendFailureType {
		LOOKUP, TRANSMISSION, SBDH, IO, OTHER
	}

	public void process() {
		IDocumentData documentData;
		while ((documentData = sendContext.getDocumentCollector().findDocument()) != null) {
			try {
				log.info("Found document to send: " + documentData.getDescription());
				DelisResponse response = sendContext.getSender().send(documentData.getInputStream());
				log.info("Sending result: " + response);
				sendContext.getResultProcessor().processResult(documentData, response);
			} catch (TransmissionLookupException e) {
				log.error("Failed lookup for " + documentData.getDescription());
				sendContext.getResultProcessor().processFailure(documentData, SendFailureType.LOOKUP, e);
			} catch (TransmissionException e) {
				log.error("Failed transmission for " + documentData.getDescription(), e);
				sendContext.getResultProcessor().processFailure(documentData, SendFailureType.TRANSMISSION, e);
			} catch (SbdhException e) {
				log.error("Failed SBDH processing for " + documentData.getDescription(), e);
				sendContext.getResultProcessor().processFailure(documentData, SendFailureType.SBDH, e);
			} catch (IOException e) {
				log.error("Failed IO operations for " + documentData.getDescription(), e);
				sendContext.getResultProcessor().processFailure(documentData, SendFailureType.IO, e);
			} catch (Throwable t) {
				sendContext.getResultProcessor().processFailure(documentData, SendFailureType.OTHER, t);
			}
		}
	}
}
