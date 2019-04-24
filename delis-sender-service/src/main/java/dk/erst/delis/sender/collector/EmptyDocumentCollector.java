package dk.erst.delis.sender.collector;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.sender.document.IDocumentData;

@Component
@ConditionalOnProperty(name="delis.sender.document.collector", havingValue="empty")
public class EmptyDocumentCollector implements IDocumentCollector {

	@Override
	public IDocumentData findDocument() {
		return null;
	}

}
