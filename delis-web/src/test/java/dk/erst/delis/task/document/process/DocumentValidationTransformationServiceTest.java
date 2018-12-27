package dk.erst.delis.task.document.process;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.data.Document;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;

public class DocumentValidationTransformationServiceTest {

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
			DocumentParseService parseService = new DocumentParseService();
			ConfigProperties configProperties = new ConfigProperties();
			configProperties.setStorageTransformationRoot("../delis-resources/transformation");
			configProperties.setStorageValidationRoot("../delis-resources/validation");
			ConfigBean configBean = new ConfigBean(configProperties);
			RuleService ruleService = new RuleService(configBean);
			DocumentBytesStorageService documentBytesStorage = new DocumentBytesStorageService(configBean);
			DocumentValidationTransformationService processService = new DocumentValidationTransformationService(ruleService, parseService, documentBytesStorage);
			
			Document d = new Document();
			d.setIngoingDocumentFormat(testDocument.getDocumentFormat());
			
			DocumentProcessLog processLog = processService.process(d, testFile);
			assertNotNull(processLog);
			
			List<DocumentProcessStep> stepList = processLog.getStepList();
			assertNotNull(stepList);
			assertTrue(!stepList.isEmpty());
			for (DocumentProcessStep step : stepList) {
				System.out.println(step);
			}
			
			assertTrue(processLog.isSuccess());
			assertNotNull(processLog.getResultPath());
			assertTrue(processLog.getResultPath().toFile().exists());
		} finally {
			TestDocumentUtil.cleanupTestFile(testFile);
		}
	}

}
