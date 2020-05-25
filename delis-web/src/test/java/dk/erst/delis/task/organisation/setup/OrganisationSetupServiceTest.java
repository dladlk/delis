package dk.erst.delis.task.organisation.setup;

import static dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup.BIS3;
import static dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup.CII;
import static dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup.OIOUBL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrganisationSetupServiceTest {

	@Autowired
	private OrganisationDaoRepository organisationDaoRepository;

	@Autowired
	private OrganisationSetupService organisationSetupService;

	private void assertStat(StatData stat, String... keyList) {
		for (String key : keyList) {
			assertStat(key, stat);
		}
	}

	private void assertStat(String key, StatData stat) {
		assertTrue("Expected stat to have positive value for a key " + key, stat.getCount(key) > 0);
	}

	@Test
	public void testLoadUpdate() {
		Organisation o = new Organisation();
		o.setCode("test" + System.currentTimeMillis());
		o.setName("test" + System.currentTimeMillis());
		organisationDaoRepository.save(o);

		OrganisationSetupData dbSetup = organisationSetupService.load(o);
		assertNotNull(dbSetup);
		assertNotNull(dbSetup.getOrganisation());

		assertEquals(OrganisationReceivingFormatRule.OIOUBL, dbSetup.getReceivingFormatRule());
		assertNull(dbSetup.getReceivingMethod());
		assertNull(dbSetup.getReceivingMethodSetup());
		assertTrue(dbSetup.getSubscribeProfileSet().isEmpty());

		OrganisationSetupData s = new OrganisationSetupData();
		s.setOrganisation(o);
		s.setReceivingFormatRule(OrganisationReceivingFormatRule.OIOUBL);
		s.setReceivingMethod(OrganisationReceivingMethod.FILE_SYSTEM);
		s.setReceivingMethodSetup("/delis/output");
		s.setSubscribeProfileSet(buildSet(BIS3, CII, OIOUBL));

		StatData stat = organisationSetupService.update(s);
		assertStat("created", stat);
		dbSetup = organisationSetupService.load(o);

		assertEqualSetup(s, dbSetup);

		s.setSubscribeProfileSet(buildSet(BIS3));
		stat = organisationSetupService.update(s);
		assertStat(stat, "unchanged", "updated");
		dbSetup = organisationSetupService.load(o);
		assertEqualSetup(s, dbSetup);

		s.setSubscribeProfileSet(Collections.emptySet());
		stat = organisationSetupService.update(s);
		assertStat(stat, "unchanged", "updated");
		dbSetup = organisationSetupService.load(o);
		assertEqualSetup(s, dbSetup);

		s.setSubscribeProfileSet(buildSet(OIOUBL));
		s.setReceivingFormatRule(OrganisationReceivingFormatRule.KEEP_ORIGINAL);
		s.setReceivingMethod(null);
		s.setReceivingMethodSetup(null);
		stat = organisationSetupService.update(s);
		assertStat(stat, "deleted", "updated");
		dbSetup = organisationSetupService.load(o);
		assertEqualSetup(s, dbSetup);
	}

	private void assertEqualSetup(OrganisationSetupData s, OrganisationSetupData dbSetup) {
		assertEquals(s.getOrganisation(), dbSetup.getOrganisation());

		assertEquals(s.getReceivingMethod(), dbSetup.getReceivingMethod());
		assertEquals(s.getSubscribeProfileSet().toString(), dbSetup.getSubscribeProfileSet().toString());
		assertEquals(s.getReceivingMethodSetup(), dbSetup.getReceivingMethodSetup());
		assertEquals(s.getReceivingFormatRule(), dbSetup.getReceivingFormatRule());
	}

	private Set<OrganisationSubscriptionProfileGroup> buildSet(OrganisationSubscriptionProfileGroup... groupList) {
		Set<OrganisationSubscriptionProfileGroup> s = new HashSet<>();
		for (OrganisationSubscriptionProfileGroup g : groupList) {
			s.add(g);
		}
		return s;
	}

}
