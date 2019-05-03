package dk.erst.delis.task.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.web.transformationrule.RuleDocumentTransformationData;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.RuleDocumentValidationData;
import dk.erst.delis.web.validationrule.ValidationRuleService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
public class RuleServiceTest {
	@Autowired
	private TransformationRuleService transformationRuleService;
	@Autowired
	private ValidationRuleService validationRuleService;
	@Autowired
	private RuleService service;


	@Test
	public void testDefaults() {
		assertEquals(15, service.getValidationList().size());
		assertEquals(2, service.getTransformationList().size());

		RuleDocumentValidationData validationData = new RuleDocumentValidationData();
		validationData.setDocumentFormat(DocumentFormat.UNSUPPORTED);
		validationData.setRootPath("root path");
		validationData.setConfig("config");
		validationData.setValidationType(RuleDocumentValidationType.XSD);
		validationData.setPriority(1);
		validationRuleService.saveRule(validationData);
		RuleDocumentTransformationData transformationData = new RuleDocumentTransformationData();
		transformationData.setConfig("config");
		transformationData.setRootPath("root path");
		transformationData.setDocumentFormatFamilyFrom(DocumentFormatFamily.OIOUBL);
		transformationData.setDocumentFormatFamilyTo(DocumentFormatFamily.OIOUBL);
		transformationRuleService.saveRule(transformationData);

		assertEquals(15, service.getValidationList().size());
		assertEquals(2, service.getTransformationList().size());

		service.refreshValidationList();
		service.refreshTransformationList();

		assertEquals(16, service.getValidationList().size());
		assertEquals(3, service.getTransformationList().size());
	}

	//todo Have to keep @SpringBootTest since it depends on actual repository
//	@BeforeClass
//	public static void init() {
//		RuleDocumentTransformationDaoRepository ruleDocumentTransformationDaoRepository = mock(RuleDocumentTransformationDaoRepository.class);
//		when(ruleDocumentTransformationDaoRepository.findAll(any(PageRequest.class))).then(d -> {
//			return new PageImpl<>(transformationRuleService.loadRulesList());
//		});
//		when(ruleDocumentTransformationDaoRepository.findAll()).then(d -> {
//			return DefaultRuleBuilder.buildDefaultTransformationRuleList();
//		});
//		RuleDocumentValidationDaoRepository ruleDocumentValidationDaoRepository = mock(RuleDocumentValidationDaoRepository.class);
//		when(ruleDocumentValidationDaoRepository.findAll(any(PageRequest.class))).then(d -> {
//			return new PageImpl<>(DefaultRuleBuilder.buildDefaultValidationRuleList());
//		});
//		when(ruleDocumentValidationDaoRepository.findAll()).then(d -> {
//			return DefaultRuleBuilder.buildDefaultValidationRuleList();
//		});
//
//		ConfigBean configBean = new ConfigBean(TestUtil.getEmptyConfigValueDaoRepository());
//		transformationRuleService = new TransformationRuleService(ruleDocumentTransformationDaoRepository);
//		validationRuleService = new ValidationRuleService(ruleDocumentValidationDaoRepository);
//		service = new RuleService(configBean, validationRuleService, transformationRuleService);
//
//	}
}
