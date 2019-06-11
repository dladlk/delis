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
                assertEquals(errorRecord.getLocation(), "line 6, column 18");
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
                assertTrue(processLog.getStepList().size() == 3);
                DocumentProcessStep documentProcessStep = processLog.getStepList().get(2);
                List<ErrorRecord> errorRecords = documentProcessStep.getErrorRecords();
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals(errorRecord.getErrorType(), DocumentErrorCode.BIS3_SCH);
                assertEquals(errorRecord.getCode(), "PEPPOL-EN16931-R020");
                assertEquals(errorRecord.getLocation(), "/Invoice/AccountingSupplierParty/Party");
                assertEquals(errorRecord.getDetailedLocation(), "/Invoice[1]/AccountingSupplierParty[1]/Party[1]");
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
                assertEquals(errorRecord.getCode(), "");
                assertEquals(errorRecord.getLocation(), "line 9, column 24");
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
                assertEquals(errorRecord.getCode(), "");
                assertEquals(errorRecord.getLocation(), "line 325, column 13");
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
                assertTrue(errorRecords.size() == 1);
                ErrorRecord errorRecord = errorRecords.get(0);
                assertEquals(errorRecord.getCode(), "BR-52");
                assertEquals(errorRecord.getLocation(), "/CrossIndustryInvoice/SupplyChainTradeTransaction/IncludedSupplyChainTradeLineItem/SpecifiedLineTradeSettlement/AdditionalReferencedDocument");
                assertEquals(errorRecord.getDetailedLocation(), "/CrossIndustryInvoice[1]/SupplyChainTradeTransaction[1]/IncludedSupplyChainTradeLineItem[1]/SpecifiedLineTradeSettlement[1]/AdditionalReferencedDocument[1]");
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
            //TestDocumentUtil.cleanupTestFile(testFile);
        }
    }

    private interface ResultChecker {
        boolean checkResult(DocumentProcessLog processLog);
    }
}
