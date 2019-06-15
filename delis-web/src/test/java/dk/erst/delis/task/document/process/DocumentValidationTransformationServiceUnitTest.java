package dk.erst.delis.task.document.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import dk.erst.delis.TestUtil;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.rule.DefaultRuleBuilder;
import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;

public class DocumentValidationTransformationServiceUnitTest {

	private static DocumentValidationTransformationService processService;

	public static DocumentValidationTransformationService getTestInstance() {
		if (processService == null) {
			init();
		}
		return processService;
	}
	
	@BeforeClass
	public static void init() {
		RuleDocumentTransformationDaoRepository ruleDocumentTransformationDaoRepository = mock(RuleDocumentTransformationDaoRepository.class);
		when(ruleDocumentTransformationDaoRepository.findAll(any(PageRequest.class))).then(d -> {
			return new PageImpl<>(DefaultRuleBuilder.buildDefaultTransformationRuleList());
		});
		when(ruleDocumentTransformationDaoRepository.findAll()).then(d -> {
			return DefaultRuleBuilder.buildDefaultTransformationRuleList();
		});
		RuleDocumentValidationDaoRepository ruleDocumentValidationDaoRepository = mock(RuleDocumentValidationDaoRepository.class);
		when(ruleDocumentValidationDaoRepository.findAll(any(PageRequest.class))).then(d -> {
			return new PageImpl<>(DefaultRuleBuilder.buildDefaultValidationRuleList());
		});
		when(ruleDocumentValidationDaoRepository.findAll()).then(d -> {
			return DefaultRuleBuilder.buildDefaultValidationRuleList();
		});

		ConfigBean configBean = new ConfigBean(TestUtil.getEmptyConfigValueDaoRepository());
		TransformationRuleService tRuleService = new TransformationRuleService(ruleDocumentTransformationDaoRepository);
		ValidationRuleService vRuleService = new ValidationRuleService(ruleDocumentValidationDaoRepository);
		RuleService ruleService = new RuleService(configBean, vRuleService, tRuleService);
		DocumentParseService parseService = new DocumentParseService();
		processService = new DocumentValidationTransformationService(ruleService, parseService);
	}
	
	@Test
	public void testAll() throws Exception {
		TestDocument[] values = TestDocument.values();
		for (TestDocument testDocument : values) {
			runCase(testDocument);
		}
	}

	@Test
	public void testCII() throws Exception {
		runCase(TestDocument.CII);
	}

	@Test
	public void testMLR() throws Exception {
		runCase(TestDocument.BIS3_MESSAGE_LEVEL_RESPONSE);
	}
	
	@Test
	public void testInvoiceResponse() throws Exception {
		runCase(TestDocument.BIS3_INVOICE_RESPONSE);
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
	
			Document d = new Document();
			d.setIngoingDocumentFormat(testDocument.getDocumentFormat());
			
			DocumentProcessLog processLog = processService.process(d, testFile, OrganisationReceivingFormatRule.OIOUBL, null);
			assertNotNull(processLog);
			
			List<DocumentProcessStep> stepList = processLog.getStepList();
			assertNotNull(stepList);
			assertTrue(!stepList.isEmpty());
			for (DocumentProcessStep step : stepList) {
				System.out.println(step);
			}
			
			assertEquals("Test document "+testDocument, testDocument.isExpectedSuccess(), processLog.isSuccess());
			assertNotNull(processLog.getResultPath());
			assertTrue(processLog.getResultPath().toFile().exists());
		} finally {
			//TestDocumentUtil.cleanupTestFile(testFile);
		}
	}

	public static DocumentValidationTransformationService getProcessService() {
		return processService;
	}
}
