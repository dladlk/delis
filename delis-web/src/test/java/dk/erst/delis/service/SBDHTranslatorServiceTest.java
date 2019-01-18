package dk.erst.delis.service;

import dk.erst.delis.task.identifier.publish.sbdh.SBDHTranslatorService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SBDHTranslatorServiceTest {

    @Test
    public void test() throws IOException {
        SBDHTranslatorService service = new SBDHTranslatorService();
        Path sourceDoc = Paths.get("D:\\Projects\\delis\\delis-resources\\examples\\xml\\OIOUBL_Invoice_v2p2.xml");
        Path targetDoc = Paths.get("D:\\Projects\\delis\\delis-resources\\examples\\xml\\OIOUBL_Invoice_v2p2_sbdh.xml");
        File targetFile = new File(targetDoc.toString());
        if(targetFile.exists()) {
            targetFile.delete();
        }
        service.addHeader(sourceDoc, targetDoc);
        String result = StringUtils.join(Files.readAllLines(targetDoc), "");
        System.out.println("result = \n" + result);
        assertTrue(result.contains("</StandardBusinessDocumentHeader>"));
    }
}
