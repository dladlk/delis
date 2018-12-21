package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class IdentifierPublishServiceTest {

    @Test
    public void test() {
        Identifier identifier = createIdentifier();
        SmpEndpoint smpEndpoint = createSmpEndpoint();
        IdentifierPublishService publishService = new IdentifierPublishService(new SmpXmlService());
        PublishReport publishReport = publishService.publish(identifier, smpEndpoint);
        boolean serviceGroupCreated = publishReport.isServiceGroupCreated();
        boolean serviceMetadataCreated = publishReport.isServiceMetadataCreated();
        System.out.println("serviceGroupCreated = " + serviceGroupCreated);
        System.out.println("serviceMetadataCreated = " + serviceMetadataCreated);
        assertTrue(serviceGroupCreated);
        assertTrue(serviceMetadataCreated);
    }

    private Identifier createIdentifier() {
        Identifier identifier = new Identifier();
        identifier.setType(TestVariables.IDENTIFIER_TYPE); // ParticipantIdentifier scheme
        identifier.setValue(TestVariables.IDENTIFIER_VALUE); // ParticipantIdentifier value
        return identifier;
    }

    private SmpEndpoint createSmpEndpoint() {
        SmpEndpoint smpEndpoint = new SmpEndpoint();
        smpEndpoint.setEndpointUrl(TestVariables.SMP_ENDPOINT_URL);
        smpEndpoint.setUserName(TestVariables.SMP_ENDPOINT_USERNAME);
        smpEndpoint.setPassword(TestVariables.SMP_ENDPOINT_PASSWORD);
        return smpEndpoint;
    }
}
