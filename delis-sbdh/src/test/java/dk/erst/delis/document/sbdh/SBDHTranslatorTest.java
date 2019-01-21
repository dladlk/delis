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
        File resourcesFolder = new File("src\\test\\resources");
        for (File file : resourcesFolder.listFiles()) {
            String sourceDocPath = file.getAbsolutePath();
            Path sourceDoc = Paths.get(sourceDocPath);
            Path targetDoc = Paths.get(sourceDocPath.substring(0, sourceDocPath.indexOf(".xml")) + "_sbdh.xml");
            File targetFile = new File(targetDoc.toString());
            if(targetFile.exists()) {
                targetFile.delete();
            }
            service.addHeader(sourceDoc, targetDoc);
            String result = new String(Files.readAllBytes(targetDoc), "utf-8");
            System.out.println("result = \n" + result);
            assertTrue(result.contains("</StandardBusinessDocumentHeader>"));
        }
    }
}
