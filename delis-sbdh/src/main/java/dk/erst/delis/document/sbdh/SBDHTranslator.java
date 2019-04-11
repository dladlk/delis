package dk.erst.delis.document.sbdh;

import dk.erst.delis.document.domibus.MetadataBuilder;
import dk.erst.delis.document.domibus.MetadataSerializer;
import eu.domibus.plugin.fs.ebms3.UserMessage;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdWriter;
import no.difi.vefa.peppol.sbdh.util.XMLStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class SBDHTranslator {

    private static final Logger log = LoggerFactory.getLogger(SBDHTranslator.class);

    public Header addHeader(Path source, Path target) {
        try (FileOutputStream targetFileStream = new FileOutputStream(target.toString())) {
            Header header = parseHeader(source);
            try (SbdWriter sbdWriter = SbdWriter.newInstance(targetFileStream, header);
                 FileInputStream sourceFileStream = new FileInputStream(source.toString())) {
                XMLStreamUtils.copy(sourceFileStream, sbdWriter.xmlWriter());
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to wrap document inside SBD (SBDH): " + ex.getMessage(), ex);
            }
            return header;
        } catch (Exception e) {
            log.error("Failed to generate SBDH for " + source, e);
        }
        return null;
    }

    private Header parseHeader(Path source) throws IOException {
        try (FileInputStream sourceFileStream = new FileInputStream(source.toString())) {
            DelisSbdhParser sbdhParser = new DelisSbdhParser();
            return sbdhParser.parse(sourceFileStream);
        }
    }

    public boolean writeMetadata(Header header, String partyId, Path target) {
        try (OutputStream fileOutputStream = new FileOutputStream(target.toString())) {
            MetadataBuilder metadataBuilder = new MetadataBuilder();
            UserMessage userMessage = metadataBuilder.buildUserMessage(header, partyId);
            MetadataSerializer metadataSerializer = new MetadataSerializer();
            metadataSerializer.serialize(userMessage, fileOutputStream);
            return true;
        } catch (Exception e) {
            log.error("Failed to write metadata: " + e.getMessage(), e);
            return false;
        }
    }
}
