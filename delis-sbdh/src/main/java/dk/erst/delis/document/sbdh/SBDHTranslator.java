package dk.erst.delis.document.sbdh;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdWriter;
import no.difi.vefa.peppol.sbdh.util.XMLStreamUtils;

public class SBDHTranslator {

	private static final Logger log = LoggerFactory.getLogger(SBDHTranslator.class);
	
    public Header addHeader(Path source, Path target) {
        try {
        	DelisSbdhParser sbdhParser = new DelisSbdhParser();
            Header header = sbdhParser.parse(new FileInputStream(source.toString()));
            FileOutputStream resultStream = new FileOutputStream(target.toString());
            try (SbdWriter sbdWriter = SbdWriter.newInstance(resultStream, header)) {
                XMLStreamUtils.copy(new FileInputStream(source.toString()), sbdWriter.xmlWriter());
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to wrap document inside SBD (SBDH): " + ex.getMessage(), ex);
            }
            return header;
        } catch (Exception e) {
            log.error("Failed to generate SBDH for "+source, e);
        }
        return null;
    }
}
