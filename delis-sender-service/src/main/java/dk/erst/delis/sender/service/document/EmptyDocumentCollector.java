package dk.erst.delis.sender.service.document;

import org.springframework.stereotype.Component;

@Component
public class EmptyDocumentCollector implements IDocumentCollector {

	@Override
	public IDocumentData findDocument() {
		return null;
	}

}
