package dk.erst.delis.sender.delis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.result.IResultProcessor;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(name = "delis.sender.document.processor", havingValue = "delis")
public class DelisResultProcessor implements IResultProcessor {

	private DelisService dbService;

	@Autowired
	public DelisResultProcessor(DelisService dbService) {
		this.dbService = dbService;
	}

	@Override
	public void processResult(IDocumentData documentData, DelisResponse response) {
		log.info("Document " + documentData + " is successfully sent with response " + response);
		dbService.markDocumentSent(documentData, response.getConversationId(), response.getTimestamp());
		dbService.createSentJournal(documentData, response);
		dbService.saveReceipt(documentData, response.primaryReceipt().getValue());
	}

	@Override
	public void processFailure(IDocumentData documentData, SendFailureType failureType, Throwable e) {
		log.info("Document " + documentData + " failed delivery with failure type " + failureType + " and exception", e);
		dbService.markDocumentFailed(documentData);
		dbService.createFailureJournal(documentData, failureType, e);
//		dbService.failurePostProcess(documentData, failureType);
	}

}
