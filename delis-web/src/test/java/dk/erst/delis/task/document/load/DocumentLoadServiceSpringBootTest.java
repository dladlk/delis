package dk.erst.delis.task.document.load;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.identifier.load.IdentifierLoadServiceTest;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import dk.erst.delis.web.organisation.OrganisationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class DocumentLoadServiceSpringBootTest {
	
	@Autowired
	private DocumentDaoRepository documentDaoRepository;

	@Autowired
	private JournalDocumentDaoRepository journalDocumentDaoRepository;

	@Autowired
	private IdentifierResolverService identifierResolverService;

	@Autowired
	private IdentifierLoadService identifierLoadService;

	@Autowired
	private OrganisationService organisationService;
	
	@Autowired
	private DocumentBytesStorageService documentBytesStorageService;

	
	@Test
	public void testLoadFile() throws IOException {
		/*
		 * Execute IdentifierLoadServiceTest to have default values for identifiers in db
		 */
		IdentifierLoadServiceTest ilsTest = new IdentifierLoadServiceTest();
		ilsTest.setOrganisationService(organisationService);
		ilsTest.setIdentifierLoadService(identifierLoadService);
		ilsTest.loadTestIdentifiers();
		
		DocumentLoadService dls = new DocumentLoadService(documentDaoRepository, journalDocumentDaoRepository, new DocumentParseService(), documentBytesStorageService, identifierResolverService);

		for (TestDocument testDocument : TestDocument.values()) {
			runCase(dls, testDocument);
		}
	}

	private void runCase(DocumentLoadService dls, TestDocument testDocument) throws IOException {
		log.info("Run DocumentLoadService unit test for document "+testDocument);
		Path testFile = TestDocumentUtil.createTestFile(testDocument);
		try {
			Document document = dls.loadFile(testFile);
			assertNotNull(document);
			assertEquals(testDocument.getDocumentFormat(), document.getIngoingDocumentFormat());
			assertEquals(DocumentStatus.LOAD_OK, document.getDocumentStatus());
			assertNotNull(document.getIngoingRelativePath());
		} finally {
			TestDocumentUtil.cleanupTestFile(testFile);
		}
	}

}
