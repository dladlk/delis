package dk.erst.delis.task.identifier.load;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.TestUtil;
import dk.erst.delis.task.identifier.load.csv.CSVIdentifierStreamReaderTest;
import dk.erst.delis.web.organisation.OrganisationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
@Transactional
@Rollback
public class IdentifierLoadServiceTest {

	@Autowired
	private IdentifierLoadService identifierLoadService;

	@Autowired
	private OrganisationService organisationService;
	
	@Test
	public void test() {
		String testCase = "LF_ISO88591.csv";
		InputStream testInputStream = TestUtil.getResourceByClass(CSVIdentifierStreamReaderTest.class, testCase);

		SyncOrganisationFact fact = loadIdentifiers(testCase, testInputStream);
		assertNotNull(fact);
		assertEquals(10, fact.getTotal());
	}
	
	public void loadTestIdentifiers() throws IOException {
		String testCase = "from_xml_examples.csv";
		InputStream testInputStream = new FileInputStream(new File("../delis-resources/examples/identifier_list/from_xml_examples.csv"));
		loadIdentifiers(testCase, testInputStream);
	}

	private SyncOrganisationFact loadIdentifiers(String testCase, InputStream testInputStream) {
		String organisationCode = new SimpleDateFormat("yyyyMMdd_hhmmss_SSS").format(Calendar.getInstance().getTime());
		
		Organisation organisation = organisationService.findOrganisationByCode(organisationCode);
		if (organisation == null) {
			organisation = new Organisation();
			organisation.setCode(organisationCode);
			organisation.setName("Test "+organisationCode);
			organisationService.saveOrganisation(organisation);
		}
		
		assertNotNull(testInputStream);

		assertNotNull(identifierLoadService);

		SyncOrganisationFact fact;
		try {
			fact = identifierLoadService.loadCSV(organisation.getCode(), testInputStream, testCase);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
		
		return fact;
	}

	public void setIdentifierLoadService(IdentifierLoadService identifierLoadService) {
		this.identifierLoadService = identifierLoadService;
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

}
