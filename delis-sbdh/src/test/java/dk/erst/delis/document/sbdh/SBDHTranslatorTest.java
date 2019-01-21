package dk.erst.delis.document.sbdh;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class SBDHTranslatorTest {

    @Test
    public void test() throws IOException {
        SBDHTranslator service = new SBDHTranslator();
        File resourcesFolder = new File("../delis-resources/examples/xml");
        String postfix = "_sbdh.xml";

        for (File file : resourcesFolder.listFiles()) {
            if(file.getName().contains(postfix)) {
                file.delete();
            }
        }

        for (File file : resourcesFolder.listFiles()) {
            String sourceDocPath = file.getCanonicalPath();
            if(sourceDocPath.contains("CII_invoice_example.xml")) {
                //skip CrossIndustryInvoice
                continue;
            }
            Path sourceDoc = Paths.get(sourceDocPath);
            Path targetDoc = Paths.get(sourceDocPath.substring(0, sourceDocPath.indexOf(".xml")) + postfix);
            File targetFile = new File(targetDoc.toString());
            service.addHeader(sourceDoc, targetDoc);
            String result = new String(Files.readAllBytes(targetDoc), "utf-8");
            System.out.println("result = \n" + result);
            assertTrue(result.contains("</StandardBusinessDocumentHeader>"));
        }
    }
}
