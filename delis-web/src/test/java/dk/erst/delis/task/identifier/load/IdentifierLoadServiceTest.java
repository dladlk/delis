package dk.erst.delis.task.identifier.load;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.TestUtil;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.SyncOrganisationFact;
import dk.erst.delis.task.identifier.load.csv.CSVIdentifierStreamReaderTest;
import dk.erst.delis.web.organisation.OrganisationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdentifierLoadServiceTest {

	@Autowired
	private IdentifierLoadService identifierLoadService;

	@Autowired
	private OrganisationService organisationService;
	
	@Test
	public void test() {
		String organisationCode = new SimpleDateFormat("yyyyMMdd_hhmmss_SSS").format(Calendar.getInstance().getTime());
		
		Organisation organisation = organisationService.findOrganisationByCode(organisationCode);
		if (organisation == null) {
			organisation = new Organisation();
			organisation.setCode(organisationCode);
			organisation.setName("Test "+organisationCode);
			organisationService.saveOrganisation(organisation);
		}
		
		String testCase = "LF_ISO88591.csv";
		InputStream testInputStream = TestUtil.getResourceByClass(CSVIdentifierStreamReaderTest.class, testCase);
		assertNotNull(testInputStream);

		assertNotNull(identifierLoadService);

		SyncOrganisationFact fact;
		try {
			fact = identifierLoadService.loadCSV(organisation.getCode(), testInputStream, testCase);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return;
		}

		assertNotNull(fact);
		assertEquals(10, fact.getTotal());
	}

}
