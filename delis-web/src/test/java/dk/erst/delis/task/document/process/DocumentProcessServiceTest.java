package dk.erst.delis.task.document.process;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.data.Document;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;

public class DocumentProcessServiceTest {

	@Test
	public void testCII() throws Exception {
		runCase(TestDocument.CII);
	}

	@Test
	public void testBIS3() throws Exception {
		runCase(TestDocument.BIS3_INVOICE);
		runCase(TestDocument.BIS3_CREDITNOTE);
	}
	@Test
	public void testOIOUBL() throws Exception {
		runCase(TestDocument.OIOUBL_INVOICE);
		runCase(TestDocument.OIOUBL_CREDITNOTE);
	}
	
	private void runCase(TestDocument testDocument) throws IOException {
		Path testFile = TestDocumentUtil.createTestFile(testDocument);
		try {
			RuleService ruleService = new RuleService();
			DocumentParseService parseService = new DocumentParseService();
			ConfigProperties configProperties = new ConfigProperties();
			configProperties.setStorageTransformationRoot("/wsh/delis/delis-resources/transformation");
			configProperties.setStorageValidationRoot("/wsh/delis/delis-resources/validation");
			ConfigBean configBean = new ConfigBean(configProperties);
			DocumentProcessService processService = new DocumentProcessService(ruleService, parseService, configBean);
			
			Document d = new Document();
			d.setIngoingDocumentFormat(testDocument.getDocumentFormat());
			
			DocumentProcessLog processLog = processService.process(d, testFile);
			assertNotNull(processLog);
			assertTrue(processLog.isSuccess());
		} finally {
			TestDocumentUtil.cleanupTestFile(testFile);
		}
	}

}
