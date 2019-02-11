package dk.erst.delis.document.sbdh.cii;

import no.difi.oxalis.sniffer.document.PlainUBLHeaderParser;
import no.difi.oxalis.sniffer.document.parsers.PEPPOLDocumentParser;
import no.difi.oxalis.sniffer.identifier.CustomizationIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;

public class CIIHeaderParser extends PlainUBLHeaderParser {

    public CIIHeaderParser(Document document, XPath xPath) {
        super(document, xPath);
    }

    @Override
    public boolean canParse() {
        return ("" + rootNameSpace()).startsWith("urn:un:unece:uncefact:data:standard:");
    }

    public PEPPOLDocumentParser createDocumentParser() {
        return new CIIDocumentParser(this);
    }

    public CustomizationIdentifier fetchCustomizationId() {
        String value = "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0";
        return CustomizationIdentifier.valueOf(value);
    }

    @Override
    public ProcessIdentifier fetchProcessTypeId() {
        String value = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";
        return ProcessIdentifier.of(value);
    }

    @Override
    public String ublVersion() {
        return "D16B";
    }
}
