package dk.erst.delis.task.document.process;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

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
    public void testXsdErrorBIS3_Invoice() throws Exception {
        runCase(TestDocument.ERROR_XSD_BIS3_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertTrue(processLog.getStepList().size() == 1);
                DocumentProcessStep documentProcessStep = processLog.getStepList().get(0);
                List<ErrorRecord> errorRecords = documentProcessStep.getErrorRecords();
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals(errorRecord.getErrorType(), DocumentErrorCode.BIS3_XSD);
                assertEquals("/Invoice", errorRecord.getLocation());
                return true;
            }
        });
    }

    @Test
    public void testSchErrorBIS3_Invoice() throws Exception {
        runCase(TestDocument.ERROR_SCH_BIS3_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertEquals(2, processLog.getStepList().size());
                DocumentProcessStep documentProcessStep = processLog.getStepList().get(1);
                List<ErrorRecord> errorRecords = documentProcessStep.getErrorRecords();
				assertEquals(9, errorRecords.size());
				ErrorRecord errorRecord = errorRecords.stream().filter(e -> e.getCode().equals("BR-CO-16")).findFirst().get();
                assertEquals(DocumentErrorCode.BIS3_SCH, errorRecord.getErrorType());
                assertEquals("BR-CO-16", errorRecord.getCode());
                assertEquals("/Invoice/LegalMonetaryTotal", errorRecord.getLocation());
                assertEquals("/Invoice[1]/LegalMonetaryTotal[1]", errorRecord.getDetailedLocation());
                return true;
            }
        });
    }

    @Test
    public void testXsdErrorOIOUBL_Invoice() throws Exception {
        runCase(TestDocument.ERROR_XSD_OIOUBL_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertTrue(processLog.getStepList().size() == 1);
                DocumentProcessStep documentProcessStepXSD = processLog.getStepList().get(0);
                assertFalse(documentProcessStepXSD.isSuccess());
                List<ErrorRecord> errorRecords = documentProcessStepXSD.getErrorRecords();
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals("cvc-complex-type.2.4.a", errorRecord.getCode());
                assertEquals("/Invoice", errorRecord.getLocation());
                return true;
            }
        });
    }

    @Test
    public void testSchErrorOIOUBL_Invoice() throws Exception {
        runCase(TestDocument.ERROR_SCH_OIOUBL_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertTrue(processLog.getStepList().size() == 2);
                DocumentProcessStep documentProcessStepXSD = processLog.getStepList().get(0);
                assertTrue(documentProcessStepXSD.isSuccess());
                DocumentProcessStep documentProcessStepSCH = processLog.getStepList().get(1);
                assertFalse(documentProcessStepSCH.isSuccess());
                List<ErrorRecord> errorRecords = documentProcessStepSCH.getErrorRecords();
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals(errorRecord.getCode(), "F-INV340");
                assertEquals(errorRecord.getLocation(), "/Invoice/cac:InvoiceLine");
                assertEquals(errorRecord.getDetailedLocation(), "/Invoice[1]/cac:InvoiceLine[2]");
                return true;
            }
        });
    }

    @Test
    public void testXsdErrorCII_Invoice() throws Exception {
        runCase(TestDocument.ERROR_XSD_CII_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertTrue(processLog.getStepList().size() == 1);
                DocumentProcessStep documentProcessStepXSD = processLog.getStepList().get(0);
                assertFalse(documentProcessStepXSD.isSuccess());
                List<ErrorRecord> errorRecords = documentProcessStepXSD.getErrorRecords();
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals("cvc-complex-type.2.4.a", errorRecord.getCode());
                assertEquals("/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeDelivery/ShipToTradeParty", errorRecord.getLocation());
                return true;
            }
        });
    }

    @Test
    public void testSchErrorCII_Invoice() throws Exception {
        runCase(TestDocument.ERROR_SCH_CII_INVOICE, new ResultChecker() {
            @Override
            public boolean checkResult(DocumentProcessLog processLog) {
                assertFalse(processLog.isSuccess());
                assertTrue(processLog.getStepList().size() == 2);
                DocumentProcessStep documentProcessStepXSD = processLog.getStepList().get(0);
                assertTrue(documentProcessStepXSD.isSuccess());
                DocumentProcessStep documentProcessStepSCH = processLog.getStepList().get(1);
                assertFalse(documentProcessStepSCH.isSuccess());
                List<ErrorRecord> errorRecords = documentProcessStepSCH.getErrorRecords();
                assertEquals(4, errorRecords.size());
                return true;
            }
        });
    }

    private void runCase(TestDocument testDocument, ResultChecker resultChecker) throws IOException {
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

            DocumentProcessLog processLog = processService.process(d, testFile, OrganisationReceivingFormatRule.OIOUBL, null);
            assertNotNull(processLog);

            List<DocumentProcessStep> stepList = processLog.getStepList();
            assertNotNull(stepList);
            assertTrue(!stepList.isEmpty());
            for (DocumentProcessStep step : stepList) {
                System.out.println(step);
            }

            resultChecker.checkResult(processLog);
        } finally {
            TestDocumentUtil.cleanupTestFile(testFile);
        }
    }

    private interface ResultChecker {
        boolean checkResult(DocumentProcessLog processLog);
    }
}
