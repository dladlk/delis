package dk.erst.delis.task.rules;

import dk.erst.delis.web.transformationrule.RuleDocumentTransformationData;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.RuleDocumentValidationData;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
public class TransformationRulesServiceTest {

	@Autowired
	private TransformationRuleService service;
	
	@Test
	public void testDefaults() {
		service.recreateDefault();
		List<RuleDocumentTransformationData> ruleDocumentTransformationData = service.loadRulesList();

		assertTrue(ruleDocumentTransformationData.size() == 2);
	}
}
