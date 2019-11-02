package dk.erst.delis.task.document.process.validate.sax;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class CurrentLocationContentHandler implements ContentHandler {

	private Stack<String> nodeStack;
	
	public CurrentLocationContentHandler() {
		this.nodeStack = new Stack<String>();
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.nodeStack.clear();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		nodeStack.push(localName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		nodeStack.pop();
	}
	
	public String getCurrentLocation() {
		StringBuilder sb = new StringBuilder();
		for (int i= 0; i < this.nodeStack.size(); i++) {
			sb.append("/");
			sb.append(this.nodeStack.elementAt(i));
		}
		return sb.toString();
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
