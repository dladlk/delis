package dk.erst.delis.validator.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.service.ValidateService;

@Controller
public class IndexController {

	@Autowired
	private DelisValidatorConfig delisValidatorConfig;

	@Autowired
	private ValidateService validateService;

	@RequestMapping(value = "/", produces = "text/plain")
	@ResponseBody()
	public String index() {
		List<RuleDocumentValidation> validationRuleList = validateService.getValidationRuleList();
		StringBuilder sb = new StringBuilder();

		sb.append("Config: ");
		sb.append(delisValidatorConfig);
		sb.append("\n");

		sb.append("Defined " + validationRuleList.size() + " rules:\n");

		for (int i = 0; i < validationRuleList.size(); i++) {
			RuleDocumentValidation r = validationRuleList.get(i);
			sb.append(i + 1);
			sb.append("\t");
			sb.append(r.getPriority());
			sb.append("\t");
			sb.append(r.getDocumentFormat());
			sb.append("\t");
			sb.append(r.getRootPath());
			sb.append("\n");
		}

		return sb.toString();
	}
}
