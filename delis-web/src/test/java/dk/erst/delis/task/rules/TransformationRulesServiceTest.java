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

import dk.erst.delis.web.transformationrule.RuleDocumentTransformationData;
import dk.erst.delis.web.transformationrule.TransformationRuleService;

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

		assertEquals(2, ruleDocumentTransformationData.size());
	}
}
