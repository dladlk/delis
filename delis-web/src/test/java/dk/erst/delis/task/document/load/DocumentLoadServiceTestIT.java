package dk.erst.delis.task.document.load;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import dk.erst.delis.TestUtil;
import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.document.sbdh.SBDHTranslator;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.Header;

@Slf4j
public class DocumentLoadServiceTestIT {

	private DocumentLoadService service;
	
	@Before
	public void setupService() {
		ConfigValueDaoRepository configRepository = TestUtil.getEmptyConfigValueDaoRepository();
		ConfigBean config = new ConfigBean(configRepository);

		final long[] documentId = new long[] {0L};
		
		final String testOrganisationCode = "test"+System.currentTimeMillis();
		
		DocumentDaoRepository documentDaoRepository = mock(DocumentDaoRepository.class);
		when(documentDaoRepository.save(any(Document.class))).then(d -> {
			Document doc = (Document) d.getArgument(0);
			documentId[0]++;
			doc.setId(documentId[0]);
			log.info("Requested to save " + doc);
			return doc;
		});

		JournalDocumentDaoRepository journalDocumentDaoRepository = mock(JournalDocumentDaoRepository.class);
		when(journalDocumentDaoRepository.save(any(JournalDocument.class))).then(d -> {
			log.info("Requested to save " + d);
			return (JournalDocument) d.getArgument(0);
		});

		IdentifierResolverService identifierResolverService = mock(IdentifierResolverService.class);
		when(identifierResolverService.resolve(any(String.class), any(String.class))).then(a -> {
			Identifier i = new Identifier();
			Organisation organisation = new Organisation();
			organisation.setCode(testOrganisationCode);
			i.setOrganisation(organisation);
			i.setType(a.getArgument(0));
			i.setValue(a.getArgument(1));
			return i;
		});

		final long[] documentBytesId = new long[] {0L};

		DocumentBytesDaoRepository documentBytesDaoRepositiry = mock(DocumentBytesDaoRepository.class);
		when(documentBytesDaoRepositiry.save(any(DocumentBytes.class))).then(d -> {
			DocumentBytes docBytes = (DocumentBytes) d.getArgument(0);
			documentBytesId[0]++;
			docBytes.setId(documentBytesId[0]);
			log.info("Requested to save " + docBytes);
			return docBytes;
		});
		DocumentBytesStorageService documentBytesStorageService = new DocumentBytesStorageService(config, documentBytesDaoRepositiry);

		service = new DocumentLoadService(documentDaoRepository, journalDocumentDaoRepository, new DocumentParseService(), documentBytesStorageService, identifierResolverService);
	}

	private boolean fullLoadFromInputImitationDone = false;
	
	@Test
	public void testLoadFromInput() throws IOException {
		Path testInputFolder;
		if (fullLoadFromInputImitationDone) {
			File testFolder = File.createTempFile(this.getClass().getSimpleName(), "document_input");
			testFolder.delete();
			assertTrue("Failed to create folder " + testFolder, testFolder.mkdirs());
			for (TestDocument testDocument : TestDocument.values()) {
				// TODO: Generate imitation of all kinds of documents
				log.info(testDocument.getFilePath());
			}
			testInputFolder = testFolder.toPath();
		} else {
			testInputFolder = Paths.get("/temp/fs/test");
		}
		if (testInputFolder.toFile().exists()) {
			StatData statData = service.loadFromInput(testInputFolder);
			System.out.println(statData);
		}
	}
	
	@Test
	public void testLoadFile() throws IOException {
		for (TestDocument testDocument : TestDocument.values()) {
			runCase(service, testDocument);
		}
	}
	
	@Test
	public void testLoadInvoiceResponse() throws IOException {
		runCase(service, TestDocument.BIS3_INVOICE_RESPONSE);
	}

	private void runCase(DocumentLoadService dls, TestDocument testDocument) throws IOException {
		log.info("Run DocumentLoadService unit test for document " + testDocument);
		Path testFile = TestDocumentUtil.createTestFile(testDocument);
		Path testSbdhFile = testFileWrapSBDH(testDocument, testFile);
		try {
			runCaseTestFile(dls, testDocument, testSbdhFile);
			runCaseTestFile(dls, testDocument, testFile);
		} finally {
			TestDocumentUtil.cleanupTestFile(testSbdhFile);
			TestDocumentUtil.cleanupTestFile(testFile);
		}
	}

	private Path testFileWrapSBDH(TestDocument testDocument, Path testFile) throws IOException {
		Path sbdhFile = File.createTempFile(testDocument.name() + "_SBDH_", ".xml").toPath();
		SBDHTranslator t = new SBDHTranslator();
		Header header = t.addHeader(testFile, sbdhFile);
		assertNotNull("Failed to generate SBDH for " + testDocument, header);
		return sbdhFile;
	}

	private void runCaseTestFile(DocumentLoadService dls, TestDocument testDocument, Path testFile) {
		Document document = dls.loadFile(testFile);
		assertNotNull(document);
		assertEquals(testDocument.getDocumentFormat(), document.getIngoingDocumentFormat());
		assertEquals(DocumentStatus.LOAD_OK, document.getDocumentStatus());
	}

}
