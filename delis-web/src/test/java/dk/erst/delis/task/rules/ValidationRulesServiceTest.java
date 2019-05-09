package dk.erst.delis.task.rules;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.config.rule.DefaultRuleBuilder;
import dk.erst.delis.web.validationrule.RuleDocumentValidationData;
import dk.erst.delis.web.validationrule.ValidationRuleService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.ANY)
public class ValidationRulesServiceTest {

	@Autowired
	private ValidationRuleService service;
	
	@Test
	public void testDefaults() {
		service.recreateDefault();
		List<RuleDocumentValidationData> ruleDocumentValidationData = service.loadRulesList();

		assertEquals(DefaultRuleBuilder.buildDefaultValidationRuleList().size(), ruleDocumentValidationData.size());
	}
}
