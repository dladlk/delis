package dk.erst.delis.task.organisation.setup;

import static org.junit.Assert.*;

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

import static dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup.*;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
public class OrganisationSetupServiceTest {

	@Autowired
	private OrganisationDaoRepository organisationDaoRepository; 
	
	@Autowired
	private OrganisationSetupService organisationSetupService; 
	
	@Test
	public void testLoadUpdate() {
		Organisation o = new Organisation();
		o.setCode("test");
		o.setName("test");
		organisationDaoRepository.save(o);
		
		OrganisationSetupData dbSetup = organisationSetupService.load(o);
		assertNotNull(dbSetup);
		assertNotNull(dbSetup.getOrganisation());
		
		assertNull(dbSetup.getReceivingFormatRule());
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
		System.out.println(stat);
		assertEquals("created: 4", stat.toString());
		dbSetup = organisationSetupService.load(o);
		
		assertEqualSetup(s, dbSetup);
		
		s.setSubscribeProfileSet(buildSet(BIS3));
		stat = organisationSetupService.update(s);
		System.out.println(stat);
		assertEquals("unchanged: 3, updated: 1", stat.toStatString());
		dbSetup = organisationSetupService.load(o);
		assertEqualSetup(s, dbSetup);
		
		s.setSubscribeProfileSet(Collections.emptySet());
		stat = organisationSetupService.update(s);
		System.out.println(stat);
		assertEquals("unchanged: 3, updated: 1", stat.toStatString());
		dbSetup = organisationSetupService.load(o);
		assertEqualSetup(s, dbSetup);
		
		s.setSubscribeProfileSet(buildSet(OIOUBL));
		s.setReceivingFormatRule(null);
		s.setReceivingMethod(null);
		s.setReceivingMethodSetup(null);
		stat = organisationSetupService.update(s);
		System.out.println(stat);
		assertEquals("deleted: 3, updated: 1", stat.toStatString());
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

	private Set<OrganisationSubscriptionProfileGroup> buildSet(OrganisationSubscriptionProfileGroup ... groupList) {
		Set<OrganisationSubscriptionProfileGroup> s = new HashSet<>();
		for (OrganisationSubscriptionProfileGroup g : groupList) {
			s.add(g);
		}
		return s;
	}

}
