package dk.erst.delis.task.document.parse;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentParseServiceTest {

	@Test
	public void testAllDocuments() throws Exception {
		TestDocument[] testDocuments = new TestDocument[] { TestDocument.OIOUBL_INVOICE, TestDocument.OIOUBL_CREDITNOTE, TestDocument.BIS3_INVOICE, TestDocument.BIS3_CREDITNOTE, TestDocument.CII };
		DocumentParseService parser = new DocumentParseService();

		for (TestDocument testDocument : testDocuments) {
			log.info("Test " + testDocument);

			DocumentInfo header = parser.parseDocumentInfo(testDocument.getInputStream());
			assertNotNull(header);
		}

	}

	@Test
	public void testOne() throws Exception {
		TestDocument testDocument = TestDocument.OIOUBL_INVOICE;
		
		DocumentParseService parser = new DocumentParseService();
		DocumentInfo header = parser.parseDocumentInfo(testDocument.getInputStream());
		assertNotNull(header);
	}

}
