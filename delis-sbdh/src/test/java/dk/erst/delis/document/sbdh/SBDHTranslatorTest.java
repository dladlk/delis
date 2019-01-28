package dk.erst.delis.document.sbdh;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SBDHTranslatorTest {

    @Test
    public void test() throws IOException, SbdhException {
        SBDHTranslator service = new SBDHTranslator();
        File resourcesFolder = new File("../delis-resources/examples/xml");
        String suffix = "_sbdh.xml";
        String metadataSuffix = "_metadata.xml";

        for (File sourceFile : resourcesFolder.listFiles()) {
            File targetFile = File.createTempFile(sourceFile.getName(), suffix);
            File metadataFile = File.createTempFile(sourceFile.getName(), metadataSuffix);
            targetFile.deleteOnExit();
            metadataFile.deleteOnExit();
            Header expected = service.addHeader(sourceFile.toPath(), targetFile.toPath());
            boolean writeMetadata = service.writeMetadata(expected, "testPartyIdValue", metadataFile.toPath());
            SbdReader reader = SbdReader.newInstance(new FileInputStream(targetFile));
            Header actual = reader.getHeader();
            assertTrue(actual.equals(expected));
            assertTrue(writeMetadata);
        }
    }
}
