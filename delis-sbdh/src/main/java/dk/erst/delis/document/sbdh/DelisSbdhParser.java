package dk.erst.delis.document.sbdh;

import dk.erst.delis.document.sbdh.cii.CIIHeaderParser;
import dk.erst.delis.document.sbdh.cii.CIINapeSpaceResolver;
import dk.erst.delis.document.sbdh.ubl.DelisInvoiceDocumentParser;
import no.difi.oxalis.sniffer.PeppolStandardBusinessHeader;
import no.difi.oxalis.sniffer.document.HardCodedNamespaceResolver;
import no.difi.oxalis.sniffer.document.PlainUBLHeaderParser;
import no.difi.oxalis.sniffer.document.parsers.PEPPOLDocumentParser;
import no.difi.vefa.peppol.common.model.Header;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class DelisSbdhParser {

	private static final DocumentBuilderFactory documentBuilderFactory;

	static {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);

		try {
			documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Unable to configure DOM parser for secure processing.", e);
		}
	}

	public DelisSbdhParser() {
		/*
		 * Overwrite constructor to surpress NoSbdhParser log warning
		 */
	}

	public Header parse(InputStream inputStream) {
		return originalParse(inputStream).toVefa();
	}

	public PeppolStandardBusinessHeader originalParse(InputStream inputStream) {
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			if(hasSBDH(document)){
				throw new AlreadySBDHException("Document already have SBDH.");
			}
			boolean isCII = isCrossIndustryInvoice(document);
			XPath xPath = XPathFactory.newInstance().newXPath();
			NamespaceContext nsContext = new HardCodedNamespaceResolver();
			if (isCII) {
				nsContext = new CIINapeSpaceResolver();
			}
			xPath.setNamespaceContext(nsContext);

			PeppolStandardBusinessHeader sbdh = PeppolStandardBusinessHeader.createPeppolStandardBusinessHeaderWithNewDate();

			// use the plain UBL header parser to decode format and create correct document
			// parser
			PlainUBLHeaderParser headerParser = new PlainUBLHeaderParser(document, xPath);
			if (isCII) {
				headerParser = new CIIHeaderParser(document, xPath);
			}
			// make sure we actually have a UBL type document
			if (headerParser.canParse()) {
				sbdh.setDocumentTypeIdentifier(headerParser.fetchDocumentTypeId().toVefa());
				sbdh.setProfileTypeIdentifier(headerParser.fetchProcessTypeId());
				// try to use a specialized document parser to fetch more document details
				PEPPOLDocumentParser documentParser = null;
				try {
					String localName = headerParser.localName();
					if ("CrossIndustryInvoice".equals(localName)) {
						documentParser = headerParser.createDocumentParser();
					} else if ("Invoice".equals(localName) || "CreditNote".equals(localName)) {
						documentParser = new DelisInvoiceDocumentParser(headerParser);
					}
				} catch (Exception ex) {
					/*
					 * allow this to happen so that "unknown" PEPPOL documents still can be used by
					 * explicitly setting sender and receiver thru API
					 */
				}
				/*
				 * However, if we found an eligible parser, we should be able to determine the
				 * sender and receiver
				 */
				if (documentParser != null) {
					try {
						sbdh.setSenderId(documentParser.getSender());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						sbdh.setRecipientId(documentParser.getReceiver());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			return sbdh;
		} catch (Exception e) {
			throw new RuntimeException("Unable to parseOld document: " + e.getMessage(), e);
		}
	}

	private boolean hasSBDH(Document document) {
		return document.getElementsByTagName("StandardBusinessDocumentHeader").getLength() > 0;
	}

	private boolean isCrossIndustryInvoice(Document document) {
		return "CrossIndustryInvoice".equalsIgnoreCase(document.getDocumentElement().getLocalName());
	}

}
