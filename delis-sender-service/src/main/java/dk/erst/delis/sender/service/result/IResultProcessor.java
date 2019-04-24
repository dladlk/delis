package dk.erst.delis.sender.service.result;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.service.document.IDocumentData;
import dk.erst.delis.sender.service.task.SendService.SendFailureType;

public interface IResultProcessor {

	void processResult(IDocumentData documentData, DelisResponse response);

	void processFailure(IDocumentData documentData, SendFailureType failureType);
}
