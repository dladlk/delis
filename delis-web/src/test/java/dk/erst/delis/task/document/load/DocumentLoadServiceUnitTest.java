package dk.erst.delis.task.document.load;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.JournalDocument;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentLoadServiceUnitTest {

	@Test
	public void testLoadFile() throws IOException {
		ConfigBean config = new ConfigBean(new ConfigProperties());

		DocumentDaoRepository documentDaoRepository = mock(DocumentDaoRepository.class);
		when(documentDaoRepository.save(any(Document.class))).then(d -> {
			log.info("Requested to save " + d);
			return (Document)d.getArgument(0);
		});
		
		JournalDocumentDaoRepository journalDocumentDaoRepository = mock(JournalDocumentDaoRepository.class);
		when(journalDocumentDaoRepository.save(any(JournalDocument.class))).then(d -> {
			log.info("Requested to save " + d);
			return (JournalDocument)d.getArgument(0);
		});

		IdentifierResolverService identifierResolverService = mock(IdentifierResolverService.class);
		when(identifierResolverService.resolve(any(String.class), any(String.class))).then(a -> {
			Identifier i = new Identifier();
			i.setOrganisation(new Organisation());
			i.setType(a.getArgument(0));
			i.setValue(a.getArgument(1));
			return i;
		});
		
		DocumentBytesStorageService documentBytesStorageService = new DocumentBytesStorageService(config);
		
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
