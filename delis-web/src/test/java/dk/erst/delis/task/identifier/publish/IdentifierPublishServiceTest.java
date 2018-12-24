package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class IdentifierPublishServiceTest {

    @Test
    public void test() {
        Identifier identifier = createIdentifier();
        SmpEndpoint smpEndpoint = createSmpEndpoint();
        IdentifierPublishService publishService = new IdentifierPublishService(new SmpXmlService());

        PublishProperties orderProperties = createPublishProperties(identifier, TestVariables.ORDER_DOC_IDENTIFIER, TestVariables.ORDER_PROCESS_IDENTIFIER);
        PublishProperties invoice1Properties = createPublishProperties(identifier, TestVariables.INVOICE1_DOC_IDENTIFIER, TestVariables.INVOICE1_PROCESS_IDENTIFIER);
        PublishProperties invoice2Properties = createPublishProperties(identifier, TestVariables.INVOICE2_DOC_IDENTIFIER, TestVariables.INVOICE2_PROCESS_IDENTIFIER);
        boolean serviceGroupCreated = publishService.publishServiceGroup(orderProperties, smpEndpoint);
        System.out.println("serviceGroupCreated = " + serviceGroupCreated);

        boolean serviceMetadataOrderCreated = publishService.publishServiceMetadata(orderProperties, smpEndpoint);
        System.out.println("serviceMetadataOrderCreated = " + serviceMetadataOrderCreated);

        boolean serviceMetadataInvoice1Created = publishService.publishServiceMetadata(invoice1Properties, smpEndpoint);
        System.out.println("serviceMetadataInvoice1Created = " + serviceMetadataInvoice1Created);

        boolean serviceMetadataInvoice2Created = publishService.publishServiceMetadata(invoice2Properties, smpEndpoint);
        System.out.println("serviceMetadataInvoice2Created = " + serviceMetadataInvoice2Created);
        assertTrue(serviceGroupCreated);
        assertTrue(serviceMetadataOrderCreated);
        assertTrue(serviceMetadataInvoice1Created);
        assertTrue(serviceMetadataInvoice2Created);
    }

    private PublishProperties createPublishProperties(Identifier identifier, String documentIdentifierValue, String processIdentifierValue) {
        PublishProperties publishProperties = new PublishProperties();
        publishProperties.setParticipantIdentifierScheme("iso6523-actorid-upis");
        publishProperties.setParticipantIdentifierValue(identifier.getType()+":"+identifier.getValue());

        publishProperties.setDocumentIdentifierScheme("busdox-docid-qns");
        publishProperties.setDocumentIdentifierValue(documentIdentifierValue);

        publishProperties.setProcessIdentifierScheme("cenbii-procid-ubl");
        publishProperties.setProcessIdentifierValue(processIdentifierValue);

        ArrayList<ServiceEndpoint> endpoints = createEndpoints();
        publishProperties.setEndpoints(endpoints);

        return publishProperties;
    }

    private ArrayList<ServiceEndpoint> createEndpoints() {
        ArrayList<ServiceEndpoint> endpoints = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date serviceActivationDate = null;
        Date serviceExpirationDate = null;
        try {
            serviceActivationDate = simpleDateFormat.parse("2018-07-01");
            serviceExpirationDate = simpleDateFormat.parse("2020-07-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ServiceEndpoint as4endpoint = new ServiceEndpoint();
        as4endpoint.setTransportProfile("peppol-transport-as4-v2_0");
        as4endpoint.setUrl("http://192.168.1.117:8080/smp-4.1.0");
        as4endpoint.setServiceDescription("Test service description");
        as4endpoint.setTechnicalContactUrl("http://example.com");
        as4endpoint.setServiceActivationDate(serviceActivationDate);
        as4endpoint.setServiceExpirationDate(serviceExpirationDate);
        as4endpoint.setRequireBusinessLevelSignature(true);
        as4endpoint.setCertificate(Base64.getDecoder().decode("dGVzdA=="));

        ServiceEndpoint as2endpoint = new ServiceEndpoint();
        as2endpoint.setTransportProfile("busdox-transport-as2-ver1p0");
        as2endpoint.setUrl("http://192.168.1.117:8080/smp-4.1.0");
        as2endpoint.setServiceDescription("Test service description");
        as2endpoint.setTechnicalContactUrl("http://example.com");
        as2endpoint.setServiceActivationDate(serviceActivationDate);
        as2endpoint.setServiceExpirationDate(serviceExpirationDate);
        as2endpoint.setRequireBusinessLevelSignature(true);
        as2endpoint.setCertificate(Base64.getDecoder().decode("dGVzdA=="));

        endpoints.add(as2endpoint);
        endpoints.add(as4endpoint);
        return endpoints;
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
