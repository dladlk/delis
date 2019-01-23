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

        for (File sourceFile : resourcesFolder.listFiles()) {
            if(sourceFile.getName().equalsIgnoreCase("CII_invoice_example.xml")) {
                //skip CrossIndustryInvoice
                continue;
            }
            File targetFile = File.createTempFile(sourceFile.getName(), suffix);
            targetFile.deleteOnExit();
            Header expected = service.addHeader(sourceFile.toPath(), targetFile.toPath());
            SbdReader reader = SbdReader.newInstance(new FileInputStream(targetFile));
            Header actual = reader.getHeader();
            assertTrue(actual.equals(expected));
        }
    }
}
