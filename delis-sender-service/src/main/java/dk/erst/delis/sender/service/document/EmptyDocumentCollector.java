package dk.erst.delis.sender.service.document;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="delis.sender.document.collector", havingValue="empty")
public class EmptyDocumentCollector implements IDocumentCollector {

	@Override
	public IDocumentData findDocument() {
		return null;
	}

}
