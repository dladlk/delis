package dk.erst.delis.sender.result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.collector.DbService;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(name = "delis.sender.document.processor", havingValue = "db")
public class DbResultProcessor implements IResultProcessor {

	private DbService dbService;

	@Autowired
	public DbResultProcessor(DbService dbService) {
		this.dbService = dbService;
	}

	@Override
	public void processResult(IDocumentData documentData, DelisResponse response) {
		log.info("Document " + documentData + " is successfully sent with response " + response);
		dbService.markDocumentSent(documentData);
		dbService.createSentJournal(documentData, response);
	}

	@Override
	public void processFailure(IDocumentData documentData, SendFailureType failureType, Throwable e) {
		log.info("Document " + documentData + " failed delivery with failure type " + failureType + " and exception", e);
		dbService.markDocumentFailed(documentData);
		dbService.createFailureJournal(documentData, failureType, e);
	}

}
