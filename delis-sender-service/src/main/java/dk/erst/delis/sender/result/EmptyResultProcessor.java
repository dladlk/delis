package dk.erst.delis.sender.result;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "delis.sender.document.processor", havingValue = "empty")
@Slf4j
public class EmptyResultProcessor implements IResultProcessor {

	@Override
	public void processResult(IDocumentData documentData, DelisResponse response) {
		log.info("Empty result processor for " + documentData);
	}

	@Override
	public void processFailure(IDocumentData documentData, SendFailureType failureType, Throwable exception) {
		log.info("Empty failure processor for " + documentData + " " + failureType);
	}

}
