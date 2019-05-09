package dk.erst.delis.task.document.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.test.AbstractDelisIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class DocumentProcessServiceTest extends AbstractDelisIntegrationTest {

	@Test
	public void testProcessLoaded() {
		StatData statData = documentProcessService.processLoaded();
		System.out.println(statData.toStatString());
		assertNotNull(statData);
		Document document = prepareTestDoc();
		statData = new StatData();
		documentProcessService.processDocument(statData, document);
		assertEquals("Document xml not found: 1", statData.toStatString());
	}

	@Before
	public void initTest() throws Exception {
		this.init();
	}

	public Document prepareTestDoc() {
		Document doc = new Document();
		doc.setOrganisation(organisation);
		doc.setDocumentStatus(DocumentStatus.VALIDATE_OK);
		doc.setName("test");
		doc.setReceiverIdentifier(identifier);
		doc.setMessageId("message_id");
		documentDaoRepository.save(doc);

		DocumentBytes documentBytes = new DocumentBytes();
		documentBytes.setDocument(doc);
		documentBytes.setType(DocumentBytesType.READY);
		documentBytesDaoRepository.save(documentBytes);

		documentBytes.setSize(1024L);
		documentBytesDaoRepository.save(documentBytes);

		return doc;
	}

}
