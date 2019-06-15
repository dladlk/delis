package dk.erst.delis.sender.result;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@ConditionalOnProperty(name = "delis.sender.document.processor", havingValue = "empty")
@Slf4j
public class EmptyResultProcessor implements IResultProcessor {

	private volatile int sentCount;
	private volatile int failedCount;

	@Override
	public void processResult(IDocumentData documentData, DelisResponse response) {
		this.sentCount++;
		log.info("[" + this.sentCount + "] Successfully sent " + documentData + " with response " + response);
	}

	@Override
	public void processFailure(IDocumentData documentData, SendFailureType failureType, Throwable e) {
		this.failedCount++;
		log.info("[" + this.sentCount + "] Failed sending of " + documentData + " with failure type " + failureType, e);
	}

	public int getTotalCount() {
		return this.sentCount + this.failedCount;
	}

}
