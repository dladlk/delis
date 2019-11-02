package dk.erst.delis.task.document.response;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.DocumentValidationTransformationServiceUnitTest;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.ApplicationResponseService.InvoiceResponseGenerationData;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;

public class ApplicationResponseServiceTest {

	private static final boolean SHOW_OUT = false;

	@Test
	public void testGenerateInvoiceResponse() throws Exception {
		DocumentBytesStorageService storageService = Mockito.mock(DocumentBytesStorageService.class);
		when(storageService.find(any(Document.class), any(DocumentFormat.class))).then(d -> {
			return new DocumentBytes();
		});
		when(storageService.load(any(DocumentBytes.class), any(OutputStream.class))).then(d -> {
			OutputStream out = d.getArgument(1);
			IOUtils.copy(TestDocument.BIS3_INVOICE.getInputStream(), out);
			return true;
		});
		
		RuleService ruleService = DocumentValidationTransformationServiceUnitTest.buildDefaultRuleService();

		DocumentValidationTransformationService validationTransformationService = DocumentValidationTransformationServiceUnitTest.getTestInstance();
		ApplicationResponseService s = new ApplicationResponseService(storageService, validationTransformationService, ruleService);
		Document document = new Document();
		document.setIngoingDocumentFormat(DocumentFormat.BIS3_INVOICE);
		InvoiceResponseGenerationData data = new InvoiceResponseGenerationData();
		data.setAction("NOA");
		data.setReason("NON");
		data.setStatus("AB");
		data.setDetailType("BT-48");
		data.setDetailValue("EU12345");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.generateApplicationResponse(document, data, baos);
		byte[] res = baos.toByteArray();
		assertNotNull(res);

		if (SHOW_OUT) {
			System.out.println(new String(res, StandardCharsets.UTF_8));
		}
		Path testPath = TestDocumentUtil.createTestFile(new ByteArrayInputStream(res), "InvoiceResponse");

		List<ErrorRecord> errorRecords = s.validateInvoiceResponse(testPath);

		for (ErrorRecord errorRecord : errorRecords) {
			System.out.println(errorRecord.getCode() + " " + errorRecord.getLocation() + " " + errorRecord.getMessage() + "[" + errorRecord.getFlag() + "]");
		}
		assertTrue(errorRecords.isEmpty());
	}

}
