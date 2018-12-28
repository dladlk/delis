package dk.erst.delis.task.identifier.publish;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import dk.erst.delis.task.identifier.publish.bdxr.SmpXmlService;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

@Slf4j
public class IdentifierPublishServiceTest {

	private boolean mockIntegration = true;

	@Test
	public void test() {
		SmpIntegrationService smpIntegrationService;
		if (mockIntegration) {
			smpIntegrationService = Mockito.mock(SmpIntegrationService.class);
			when(smpIntegrationService.create(any(SmpEndpoint.class), any(String.class), any(String.class))).then(d -> {
				SmpEndpoint smpEndpoint = d.getArgument(0);
				String url = d.getArgument(1);
				String data = d.getArgument(2);
				log.info("Requested to create at " + smpEndpoint + " on " + url + " data:\n" + data);
				return true;
			});
		} else {
			smpIntegrationService = new SmpIntegrationService();
		}

		SmpXmlService smpXmlService = new SmpXmlService();
		IdentifierPublishService publishService = new IdentifierPublishService(smpXmlService, smpIntegrationService);

		ParticipantIdentifier identifier = createIdentifier();
		SmpEndpoint smpEndpoint = createSmpEndpoint();

		PublishProperties orderProperties = createPublishProperties(TestVariables.ORDER_DOC_IDENTIFIER, TestVariables.ORDER_PROCESS_IDENTIFIER);
		PublishProperties invoice1Properties = createPublishProperties(TestVariables.INVOICE1_DOC_IDENTIFIER, TestVariables.INVOICE1_PROCESS_IDENTIFIER);
		PublishProperties invoice2Properties = createPublishProperties(TestVariables.INVOICE2_DOC_IDENTIFIER, TestVariables.INVOICE2_PROCESS_IDENTIFIER);

		assertTrue(publishService.publishServiceGroup(identifier, orderProperties, smpEndpoint));
		assertTrue(publishService.publishServiceMetadata(identifier, orderProperties, smpEndpoint));
		assertTrue(publishService.publishServiceMetadata(identifier, invoice1Properties, smpEndpoint));
		assertTrue(publishService.publishServiceMetadata(identifier, invoice2Properties, smpEndpoint));
	}

	private PublishProperties createPublishProperties(String documentIdentifierValue, String processIdentifierValue) {
		PublishProperties publishProperties = new PublishProperties();

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

	private ParticipantIdentifier createIdentifier() {
		return ParticipantIdentifier.of(TestVariables.IDENTIFIER_TYPE + "::" + TestVariables.IDENTIFIER_VALUE);
	}

	private SmpEndpoint createSmpEndpoint() {
		SmpEndpoint smpEndpoint = new SmpEndpoint();
		smpEndpoint.setEndpointUrl(TestVariables.SMP_ENDPOINT_URL);
		smpEndpoint.setUserName(TestVariables.SMP_ENDPOINT_USERNAME);
		smpEndpoint.setPassword(TestVariables.SMP_ENDPOINT_PASSWORD);
		return smpEndpoint;
	}
}
