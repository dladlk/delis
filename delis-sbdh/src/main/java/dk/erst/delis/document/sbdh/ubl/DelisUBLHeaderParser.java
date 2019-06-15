package dk.erst.delis.document.sbdh.ubl;

import no.difi.oxalis.sniffer.document.PlainUBLHeaderParser;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;

public class DelisUBLHeaderParser extends PlainUBLHeaderParser {
    public DelisUBLHeaderParser(Document document, XPath xPath) {
        super(document, xPath);
    }

    @Override
    public String ublVersion() {
        return StringUtils.defaultIfBlank(super.ublVersion(), "2.1");
    }
}
