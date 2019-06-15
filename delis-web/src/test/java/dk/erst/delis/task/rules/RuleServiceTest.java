package dk.erst.delis.task.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.config.rule.DefaultRuleBuilder;
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
		int validationSize = DefaultRuleBuilder.buildDefaultValidationRuleList().size();
		int transformationSize = DefaultRuleBuilder.buildDefaultTransformationRuleList().size();
		
		assertEquals(validationSize, service.getValidationList().size());
		assertEquals(transformationSize, service.getTransformationList().size());

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

		assertEquals(validationSize, service.getValidationList().size());
		assertEquals(transformationSize, service.getTransformationList().size());

		service.refreshValidationList();
		service.refreshTransformationList();

		assertEquals(validationSize + 1, service.getValidationList().size());
		assertEquals(transformationSize + 1, service.getTransformationList().size());
	}

}
