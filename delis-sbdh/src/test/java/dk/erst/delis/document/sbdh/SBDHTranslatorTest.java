package dk.erst.delis.document.sbdh;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertTrue;

public class SBDHTranslatorTest {

    private static final Logger log = LoggerFactory.getLogger(SBDHTranslatorTest.class);

    @Test
    public void test() throws IOException, SbdhException {
        SBDHTranslator service = new SBDHTranslator();
        File resourcesFolder = new File("../delis-resources/examples/xml");
        String suffix = "_sbdh.xml";
        String metadataSuffix = "_metadata.xml";

        for (File sourceFile : resourcesFolder.listFiles(pathname -> pathname.isFile())) {
            log.info("Testing " + sourceFile);

            File tempSourceFile = File.createTempFile(sourceFile.getName(), "tmp.xml");
            Files.copy(sourceFile.toPath(), tempSourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            File targetFile = File.createTempFile(sourceFile.getName(), suffix);
            File metadataFile = File.createTempFile(sourceFile.getName(), metadataSuffix);
            System.out.println("Prepared temp source file " + tempSourceFile);
            Header expected;
            try {
                expected = service.addHeader(tempSourceFile.toPath(), targetFile.toPath());
            } finally {
                assertTrue(tempSourceFile.delete());
            }
            boolean writeMetadata = service.writeMetadata(expected, "testPartyIdValue", metadataFile.toPath());
            FileInputStream inputStream = new FileInputStream(targetFile);
            SbdReader reader = SbdReader.newInstance(inputStream);
            inputStream.close();
            Header actual = reader.getHeader();
            assertTrue(actual.equals(expected));
            assertTrue(writeMetadata);
            assertTrue(targetFile.delete());
            assertTrue(metadataFile.delete());
        }
    }
}
