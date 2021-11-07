package dk.erst.delis.task.document.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.parse.data.DocumentParticipant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentParseServiceTest {

	@Test
	public void testAllDocuments() throws Exception {
		TestDocument[] testDocuments = new TestDocument[] { TestDocument.OIOUBL_INVOICE, TestDocument.OIOUBL_CREDITNOTE, TestDocument.BIS3_INVOICE, TestDocument.BIS3_CREDITNOTE, TestDocument.CII ,
				
				TestDocument.BIS3_ORDER_ONLY, TestDocument.BIS3_ORDER_ORDERING, TestDocument.BIS3_ORDER_RESPONSE, TestDocument.BIS3_CATALOGUE_ONLY, TestDocument.BIS3_CATALOGUE_RESPONSE_ONLY,
				
				TestDocument.BIS3_CATALOGUE_WITHOUT_RESPONSE,
				
		};
		DocumentParseService parser = new DocumentParseService();

		for (TestDocument testDocument : testDocuments) {
			log.info("Test " + testDocument);

			DocumentInfo header = parser.parseDocumentInfo(testDocument.getInputStream());
			assertFilledHeader(header);
			assertFalse(header.isAmountNegative());
		}

	}

	@Test
	public void testOne() throws Exception {
		TestDocument testDocument = TestDocument.OIOUBL_INVOICE;
		
		DocumentParseService parser = new DocumentParseService();
		DocumentInfo header = parser.parseDocumentInfo(testDocument.getInputStream());
		assertFilledHeader(header);
	}
	
	@Test
	public void testInvoiceResponse() throws Exception {
		TestDocument testDocument = TestDocument.BIS3_INVOICE_RESPONSE;
		
		DocumentParseService parser = new DocumentParseService();
		DocumentInfo header = parser.parseDocumentInfo(testDocument.getInputStream());
		assertFilledHeader(header);
	}
	
	private void assertFilledHeader(DocumentInfo header) {
		assertNotNull(header);
		assertNotEmpty(header.getDate());
		assertNotEmpty(header.getId());
		assertNotNull(header.getProfile());
		assertParticipant(header.getSender());
		assertParticipant(header.getReceiver());
		assertNotEmpty(header.getCustomizationID());
	}

	private void assertParticipant(DocumentParticipant p) {
		assertNotNull(p);
		assertNotEmpty(p.getId());
		assertNotEmpty(p.getSchemeId());
	}
	
	private void assertNotEmpty(String s) {
		assertNotNull(s);
		assertTrue(s.trim().length() > 0);
	}

}
