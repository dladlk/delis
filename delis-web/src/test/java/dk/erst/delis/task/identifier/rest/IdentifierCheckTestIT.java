package dk.erst.delis.task.identifier.rest;

import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.identifier.load.IdentifierLoadServiceTestIT;
import dk.erst.delis.web.identifier.IdentifierCheckRestController;
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

	@Before
	public void init() throws Exception{
		IdentifierLoadServiceTestIT ilsTest = new IdentifierLoadServiceTestIT();
		ilsTest.setOrganisationService(organisationService);
		ilsTest.setIdentifierLoadService(identifierLoadService);
		ilsTest.loadTestIdentifiers();
	}

	@Test
	public void testOK() {

		ResponseEntity responseEntity = idController.checkIdentifier("0088:5790000436057");

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void test204() {

		ResponseEntity responseEntity = idController.checkIdentifier("something:wrong");

		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
}
