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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.dao.DocumentRepository;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DocumentLoadServiceDbTest {
	
	@Autowired
	private DocumentRepository documentRepository;

	@Test
	public void testLoadFile() throws IOException {
		ConfigBean config = new ConfigBean(new ConfigProperties());

		DocumentLoadService dls = new DocumentLoadService(documentRepository, new DocumentParseService(), config);

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
