package dk.erst.delis.web.organisation;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.web.organisation.OrganisationStatisticsService.OrganisationIdentifierStatData;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OrganisationStatisticsServiceTest {

	@Autowired
	private OrganisationStatisticsService organisationStatisticsService;
	
	@Test
	public void testLoadOrganisationIdentifierStatMap() {
		Map<Long, OrganisationIdentifierStatData> map = organisationStatisticsService.loadOrganisationIdentifierStatMap();
		for (Long organisationId : map.keySet()) {
			OrganisationIdentifierStatData statData = map.get(organisationId);
			System.out.println(statData);
			
			OrganisationIdentifierStatData orgStat = organisationStatisticsService.loadOrganisationIdentifierStatMap(organisationId);
			
			assertEquals(statData.toString(), orgStat.toString());
		}
	}

}
