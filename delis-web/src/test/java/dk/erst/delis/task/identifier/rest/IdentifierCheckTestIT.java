package dk.erst.delis.task.identifier.rest;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.rest.IdentifierCheckRestController;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.identifier.load.IdentifierLoadServiceTestIT;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.organisation.OrganisationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
@Transactional
@Rollback
public class IdentifierCheckTestIT {

	@Autowired
	private IdentifierLoadService identifierLoadService;

	@Autowired
	private OrganisationService organisationService;

	@Autowired
	private IdentifierCheckRestController idController;

	@Autowired
	private OrganisationSetupService organisationSetupService;

	@Before
	public void init() throws Exception{
		IdentifierLoadServiceTestIT ilsTest = new IdentifierLoadServiceTestIT();
		ilsTest.setOrganisationService(organisationService);
		ilsTest.setIdentifierLoadService(identifierLoadService);
		ilsTest.loadTestIdentifiers();
		Iterable<Organisation> organisations = organisationService.getOrganisations();
		for (Organisation org : organisations) {
			OrganisationSetupData setupData = organisationSetupService.load(org);
			Set<OrganisationSubscriptionProfileGroup> profileSet = new HashSet<>();
			profileSet.add(CII);
			profileSet.add(BIS3);
			profileSet.add(OIOUBL);
			setupData.setSubscribeProfileSet(profileSet);

			organisationSetupService.update(setupData);
		}
	}

	@Test
	public void testOK() {

		ResponseEntity responseEntity = idController.checkIdentifier("0088:5790000436057",
				"urn:www.nesubl.eu:profiles:profile5:ver2.0",
				"busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0");

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void test204() {

		ResponseEntity responseEntity = idController.checkIdentifier("something:wrong", "", "");

		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
}
