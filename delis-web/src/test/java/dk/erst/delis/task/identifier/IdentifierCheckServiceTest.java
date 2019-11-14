package dk.erst.delis.task.identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.identifier.IdentifierValueType;
import dk.erst.delis.task.identifier.IdentifierCheckService.Result;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;

public class IdentifierCheckServiceTest {

	@Test
	public void testCheckReceivingSupport() {
		Organisation organisation = new Organisation();
		final Identifier identifier = new Identifier();
		identifier.setOrganisation(organisation);
		identifier.setType(IdentifierValueType.GLN.getCode());
		identifier.setValue("TEST");

		OrganisationSetupData setupData = new OrganisationSetupData();
		final OrganisationSetupData setup[] = new OrganisationSetupData[] { setupData };

		IdentifierResolverService identifierResolverService = mock(IdentifierResolverService.class);
		when(identifierResolverService.resolve(any(String.class), any(String.class))).then(d -> {
			String type = (String) d.getArgument(0);
			String id = (String) d.getArgument(1);
			if ("0088".equals(type) && identifier.getValue().equals(id)) {
				return identifier;
			}
			return null;
		});

		OrganisationSetupService organisationSetupService = mock(OrganisationSetupService.class);
		when(organisationSetupService.load(any(Organisation.class))).then(d -> {
			return setup[0];
		});

		IdentifierCheckService service = new IdentifierCheckService(identifierResolverService, organisationSetupService);
		/*
		 * Unknown identifier
		 */
		assertReject("Identifier '0088:Undefined' does not exist", service.checkReceivingSupport("0088:Undefined", "Wrong", "Wrong", false, false));
		/*
		 * Deleted identifier
		 */
		identifier.setStatus(IdentifierStatus.DELETED);
		assertFalse(service.checkReceivingSupport("0088:TEST", "Wrong", "Wrong", true, true).isSuccess());
		/*
		 * Active identifier
		 */
		identifier.setStatus(IdentifierStatus.ACTIVE);
		assertTrue(service.checkReceivingSupport("0088:TEST", "Wrong", "Wrong", true, true).isSuccess());

		/*
		 * No profile is configured
		 */
		assertFalse(service.checkReceivingSupport("0088:TEST", "nes-procid-ubl::urn:www.nesubl.eu:profiles:profile5:ver2.0", "busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", false, false).isSuccess());
		/*
		 * Correct profile is configured
		 */
		setupData.getSubscribeProfileSet().add(OrganisationSubscriptionProfileGroup.OIOUBL);
		/*
		 * With scheme in identifier and process
		 */
		assertTrue(service.checkReceivingSupport("0088:TEST", "nes-procid-ubl::urn:www.nesubl.eu:profiles:profile5:ver2.0", "busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", false, false).isSuccess());
		/*
		 * Without scheme in identifier and process
		 */
		assertTrue(service.checkReceivingSupport("0088:TEST", "urn:www.nesubl.eu:profiles:profile5:ver2.0", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", false, false).isSuccess());
		/*
		 * Wrong document type
		 */
		assertFalse(service.checkReceivingSupport("0088:TEST", "urn:www.nesubl.eu:profiles:profile5:ver2.0", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", false, false).isSuccess());
		/*
		 * Skip service test
		 */
		assertTrue(service.checkReceivingSupport("0088:TEST", "urn:www.nesubl.eu:profiles:profile5:ver2.0", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", true, false).isSuccess());
		assertTrue(service.checkReceivingSupport("0088:TEST", "Wrong", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", true, false).isSuccess());
		assertTrue(service.checkReceivingSupport("0088:TEST", null, "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", true, false).isSuccess());
		/*
		 * Skip action test
		 */
		assertTrue(service.checkReceivingSupport("0088:TEST", null, null, true, true).isSuccess());
		assertTrue(service.checkReceivingSupport("0088:TEST", "urn:www.nesubl.eu:profiles:profile5:ver2.0", null, false, true).isSuccess());
		assertTrue(service.checkReceivingSupport("0088:TEST", "nes-procid-ubl::urn:www.nesubl.eu:profiles:profile5:ver2.0", null, false, true).isSuccess());
		assertTrue(service.checkReceivingSupport("0088:TEST", "cenbii-procid-ubl::urn:www.nesubl.eu:profiles:profile5:ver2.0", null, false, true).isSuccess());
		assertFalse(service.checkReceivingSupport("0088:TEST", "cenbii-procid-ubl::wrong", null, false, true).isSuccess());
		assertFalse(service.checkReceivingSupport("0088:TEST", "cenbii-procid-ubl::", null, false, true).isSuccess());
		assertFalse(service.checkReceivingSupport("0088:TEST", "::", null, false, true).isSuccess());
		assertFalse(service.checkReceivingSupport("0088:TEST", ":", null, false, true).isSuccess());
		assertFalse(service.checkReceivingSupport("0088:TEST", null, null, false, true).isSuccess());
		/*
		 * Invalid identifier structure
		 */
		assertFalse(service.checkReceivingSupport(null, null, null, true, true).isSuccess());
		assertFalse(service.checkReceivingSupport("", null, null, true, true).isSuccess());
		assertFalse(service.checkReceivingSupport(":", null, null, true, true).isSuccess());
		assertFalse(service.checkReceivingSupport(":A", null, null, true, true).isSuccess());
		assertFalse(service.checkReceivingSupport("A:", null, null, true, true).isSuccess());
	}

	private void assertReject(String description, Result r) {
		assertFalse(r.isSuccess());
		assertEquals(description, r.getDescription());
	}

}
