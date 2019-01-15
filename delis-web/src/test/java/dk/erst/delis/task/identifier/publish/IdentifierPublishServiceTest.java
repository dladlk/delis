package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.codelist.CodeListReaderService;
import dk.erst.delis.task.identifier.publish.bdxr.SmpXmlService;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
public class IdentifierPublishServiceTest {

	private boolean mockIntegration = false;

	@Test
	public void test() {
		SmpIntegrationService smpIntegrationService;
		SmpLookupService smpLookupService;
		ConfigBean configBean = new ConfigBean(new ConfigProperties());
		configBean.getSmpEndpointConfig().setUrl("http://localhost:8080/smp-4.1.0");
		if (mockIntegration) {
			smpIntegrationService = Mockito.mock(SmpIntegrationService.class);
			when(smpIntegrationService.create(any(String.class), any(String.class))).then(d -> {
				String url = d.getArgument(0);
				String data = d.getArgument(1);
				log.info("Requested to create on " + url + " data:\n" + data);
				return true;
			});
			when(smpIntegrationService.delete(any(String.class))).then(d -> {
				String url = d.getArgument(0);
				log.info("Requested to delete on " + url);
				return true;
			});
		} else {
			smpIntegrationService = new SmpIntegrationService(configBean);
		}
		
//		smpLookupService = Mockito.mock(SmpLookupService.class);
		smpLookupService = new SmpLookupService(configBean);

		/*when(smpLookupService.lookup(any(ParticipantIdentifier.class))).then(d -> {
			ParticipantIdentifier identifier = d.getArgument(0);
			return null;
		});*/

		SmpXmlService smpXmlService = new SmpXmlService();
		CodeListDict codeListDict = new CodeListDict(new CodeListReaderService(configBean));
		IdentifierPublishDataService identifierPublishDataService = new IdentifierPublishDataService(codeListDict);
		IdentifierPublishService publishService = new IdentifierPublishService(smpXmlService, smpIntegrationService, identifierPublishDataService, smpLookupService);

		Identifier identifier = new Identifier();
		identifier.setType("GLN");
		identifier.setValue("tbcntrl00002");
		identifier.setStatus(IdentifierStatus.ACTIVE);
		identifier.setPublishingStatus(IdentifierPublishingStatus.PENDING);
		assertTrue(publishService.publishIdentifier(identifier));

		ParticipantIdentifier testParticipantIdentifier = identifierPublishDataService.buildPublishData(identifier).getParticipantIdentifier();
		SmpPublishData lookup = smpLookupService.lookup(testParticipantIdentifier);
		assertNotNull(lookup);
	}

}
