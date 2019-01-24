package dk.erst.delis.document.sbdh;

import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.sniffer.document.NoSbdhParser;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdWriter;
import no.difi.vefa.peppol.sbdh.util.XMLStreamUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class SBDHTranslator {

    public Header addHeader(Path source, Path target) {
        try {
            NoSbdhParser sbdhParser = new DelisSbdhParser();
            Header header = sbdhParser.parse(new FileInputStream(source.toString()));
            FileOutputStream resultStream = new FileOutputStream(target.toString());
            try (SbdWriter sbdWriter = SbdWriter.newInstance(resultStream, header)) {
                XMLStreamUtils.copy(new FileInputStream(source.toString()), sbdWriter.xmlWriter());
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to wrap document inside SBD (SBDH). " + ex.getMessage(), ex);
            }
            return header;
        } catch (IOException | OxalisContentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
