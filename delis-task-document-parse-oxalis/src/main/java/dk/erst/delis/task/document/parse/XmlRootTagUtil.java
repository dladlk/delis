package dk.erst.delis.task.document.parse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlRootTagUtil {

	public static InputStream fillRootTagData(InputStream inputStream, XmlElementData ed) throws Exception {
		OptionalClosableInputStream bis = new OptionalClosableInputStream(inputStream);
		XmlElementData parsed = getRootTagElementData(bis);
		if (parsed != null) {
			ed.setLocalName(parsed.getLocalName());
			ed.setNamespaceUri(parsed.getNamespaceUri());
			ed.setQualifiedName(parsed.getQualifiedName());
		} else {
			ed.setLocalName(null);
			ed.setNamespaceUri(null);
			ed.setQualifiedName(null);
		}
		return bis;
	}

	private static XmlElementData getRootTagElementData(OptionalClosableInputStream bis) throws IOException {
		bis.mark(1024 * 1024);
		bis.setCanClose(false);
		XmlElementData rootTagDataOnly;
		try {
			rootTagDataOnly = XmlRootTagUtil.parseRootTagDataOnly(bis);
		} finally {
			bis.reset();
			bis.setCanClose(true);
		}
		return rootTagDataOnly;
	}

	private static XmlElementData parseRootTagDataOnly(InputStream input) {
		final SaxRootTagExtractorHandler handler = new SaxRootTagExtractorHandler();
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		final SAXParser parser;

		try {
			parser = factory.newSAXParser();
			parser.parse(input, handler);
		} catch (RootTagFoundException e) {
			return e.getRootTagData();
		} catch (SAXParseException e) {
			log.warn("Failed to parse root tag data, consider as non-XML document: " + e.getMessage());
			/*
			 * Not XML file, just return default value.
			 */
		} catch (Exception e) {
			/*
			 * Just log problem
			 */
			log.warn("Failed to parse root tag data, consider as non-XML document: " + e.getMessage());
		}
		return null;
	}

	public static class SaxRootTagExtractorHandler extends DefaultHandler {
		@Override
		public void startElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
			throw new RootTagFoundException(namespaceUri, localName, qualifiedName, attributes);
		}
	}

	protected static class RootTagFoundException extends SAXException {

		private static final long serialVersionUID = 8444530780035647600L;

		private final XmlElementData rootTagData;

		public RootTagFoundException(String namespaceUri, String localName, String qualifiedName, Attributes attributes) {
			super("");
			this.rootTagData = new XmlElementData(namespaceUri, localName, qualifiedName, attributes);
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public XmlElementData getRootTagData() {
			return rootTagData;
		}

	}

	public static class XmlElementData implements Serializable {

		private static final long serialVersionUID = 221785036169725024L;

		protected String namespaceUri;
		protected String localName;
		protected String qualifiedName;
		protected Attributes attributes;

		public XmlElementData() {

		}

		public XmlElementData(String namespaceUri, String localName, String qualifiedName, Attributes attributes) {
			this.namespaceUri = namespaceUri;
			this.localName = localName;
			this.qualifiedName = qualifiedName;
			this.attributes = attributes;
		}

		public String getNamespaceUri() {
			return namespaceUri;
		}

		public String getLocalName() {
			return localName;
		}

		public String getQualifiedName() {
			return qualifiedName;
		}

		public Attributes getAttributes() {
			return attributes;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("namespace=");
			builder.append(namespaceUri);
			builder.append(", localName=");
			builder.append(localName);
			return builder.toString();
		}

		public void setNamespaceUri(String namespaceUri) {
			this.namespaceUri = namespaceUri;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}

		public void setQualifiedName(String qualifiedName) {
			this.qualifiedName = qualifiedName;
		}

		public void setAttributes(Attributes attributes) {
			this.attributes = attributes;
		}

		public boolean isFilled() {
			return this.localName != null;
		}
	}

	private static final class OptionalClosableInputStream extends BufferedInputStream {

		private boolean canClose = true;

		public OptionalClosableInputStream(InputStream in) {
			super(in, 8 * 1024);
		}

		@Override
		public void close() throws IOException {
			if (canClose) {
				super.close();
			}
		}

		public void setCanClose(boolean canClose) {
			this.canClose = canClose;
		}

	}
}
