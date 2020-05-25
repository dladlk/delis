package dk.erst.delis.web.validationrule;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.task.document.process.RuleService;

public class ValidationRuleDataValidator implements Validator {

	private RuleService ruleService;

	public ValidationRuleDataValidator(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RuleDocumentValidationData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (errors.hasErrors()) {
			return;
		}
		RuleDocumentValidationData d = (RuleDocumentValidationData)target;

		if (d.isActive()) {
			List<RuleDocumentValidation> dbList = ruleService.loadDbValidationList();
			
			Optional<RuleDocumentValidation> samePriority = dbList.stream().filter(s -> s.isActive() && s.getDocumentFormat() == d.getDocumentFormat() && s.getValidationType() == d.getValidationType() && s.getPriority() == d.getPriority()).findAny();
			if (samePriority.isPresent() && !samePriority.get().getId().equals(d.getId())) {
				errors.rejectValue("priority", "rule.validation.priority_duplicate");
			}
		}
		
		RuleDocumentValidation rdt = new RuleDocumentValidation();
		rdt.setRootPath(d.getRootPath());
		
		Path root = ruleService.getStorageValidationPath();
		
		Path filePath = ruleService.filePath(rdt);
		if (!filePath.toFile().exists() || !filePath.toFile().isFile()) {
			errors.rejectValue("rootPath", "rule.path.not_accessible");
		}
		
		if (!filePath.normalize().startsWith(root.normalize())) {
			errors.rejectValue("rootPath", "rule.path.not_nested");
		}
		
	}

}
