package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SmpXmlServiceTest {

    @Test
    public void test() {
        Identifier testIdentifier = new Identifier();
        testIdentifier.setType(TestVariables.IDENTIFIER_TYPE);
        testIdentifier.setValue(TestVariables.IDENTIFIER_VALUE);
        SmpXmlService smpXmlService = new SmpXmlService();
//        String serviceGroupXml = smpXmlService.createServiceGroupXml(testIdentifier);
//        assertTrue(serviceGroupXml.contains(testIdentifier.getType()));
//        assertTrue(serviceGroupXml.contains(testIdentifier.getValue()));
//        String serviceMetadataXml = smpXmlService.createServiceMetadataXml(testIdentifier);
//        assertTrue(serviceMetadataXml.contains(testIdentifier.getType()));
//        assertTrue(serviceMetadataXml.contains(testIdentifier.getValue()));

    }
}
