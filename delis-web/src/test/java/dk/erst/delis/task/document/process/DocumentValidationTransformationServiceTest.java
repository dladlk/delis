package dk.erst.delis.task.document.process;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DocumentValidationTransformationServiceTest {

	@Autowired
	private RuleDocumentTransformationDaoRepository tRuleRepository;

	@Autowired
	private RuleDocumentValidationDaoRepository vRuleRepository;
	
	@Autowired
	private ConfigValueDaoRepository configRepository;

	@Test
	public void testCII() throws Exception {
		runCase(TestDocument.CII);
	}

	@Test
	public void testBIS3_Invoice() throws Exception {
		runCase(TestDocument.BIS3_INVOICE);
	}

	@Test
	public void testBIS3_CreditNote() throws Exception {
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
			ConfigBean configBean = new ConfigBean(configRepository);
			TransformationRuleService tRuleService = new TransformationRuleService(tRuleRepository);
			ValidationRuleService vRuleService = new ValidationRuleService(vRuleRepository);
			RuleService ruleService = new RuleService(configBean, vRuleService, tRuleService);
			DocumentValidationTransformationService processService = new DocumentValidationTransformationService(ruleService, parseService);
			
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
			//TestDocumentUtil.cleanupTestFile(testFile);
		}
	}
}
