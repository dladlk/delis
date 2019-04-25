package dk.erst.delis.sender.result;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.service.SendService.SendFailureType;

public interface IResultProcessor {

	void processResult(IDocumentData documentData, DelisResponse response);

	void processFailure(IDocumentData documentData, SendFailureType failureType, Throwable exception);
}
