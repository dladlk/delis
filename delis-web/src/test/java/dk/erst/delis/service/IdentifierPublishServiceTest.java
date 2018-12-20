package dk.erst.delis.service;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.SmpEndpoint;
import dk.erst.delis.task.identifier.publish.IdentifierPublishService;
import dk.erst.delis.task.identifier.publish.PublishReport;
import dk.erst.delis.xml.SmpXmlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class IdentifierPublishServiceTest {

    @Test
    public void test() {
        Identifier identifier = new Identifier();
        identifier.setType("0088");
        identifier.setValue("test1");
        SmpEndpoint smpEndpoint = new SmpEndpoint();
        IdentifierPublishService publishService = new IdentifierPublishService(new SmpXmlService());
        PublishReport publishReport = publishService.publish(identifier, smpEndpoint);
        assertTrue(publishReport.isServiceGroupCreated());
        assertTrue(publishReport.isServiceMetadataCreated());

    }
}
