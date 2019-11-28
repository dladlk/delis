package dk.erst.delis.web.transformationrule;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.task.document.process.RuleService;

public class TransformationRuleDataValidator implements Validator {

	private RuleService ruleService;

	public TransformationRuleDataValidator(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RuleDocumentTransformationData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (errors.hasErrors()) {
			return;
		}
		RuleDocumentTransformationData d = (RuleDocumentTransformationData)target;
		
		if (d.getDocumentFormatFamilyFrom() == d.getDocumentFormatFamilyTo()) {
			errors.rejectValue("documentFormatFamilyTo", "rule.transformation.formats_should_be_different");
		}

		if (d.isActive()) {
			List<RuleDocumentTransformation> dbList = ruleService.loadDbTransformationList();
			
			Optional<RuleDocumentTransformation> sameFormats = dbList.stream().filter(s -> s.isActive() && s.getDocumentFormatFamilyFrom() == d.getDocumentFormatFamilyFrom() && s.getDocumentFormatFamilyTo() == d.getDocumentFormatFamilyTo()).findAny();
			if (sameFormats.isPresent() && !sameFormats.get().getId().equals(d.getId())) {
				errors.rejectValue("documentFormatFamilyTo", "rule.transformation.formats_duplicate");
			}
	
			Optional<RuleDocumentTransformation> oppositeFormats = dbList.stream().filter(s -> s.isActive() && s.getDocumentFormatFamilyFrom() == d.getDocumentFormatFamilyTo() && s.getDocumentFormatFamilyTo() == d.getDocumentFormatFamilyFrom()).findAny();
			if (oppositeFormats.isPresent()) {
				errors.rejectValue("documentFormatFamilyTo", "rule.transformation.formats_cycle");
			}
		}
		
		RuleDocumentTransformation rdt = new RuleDocumentTransformation();
		rdt.setRootPath(d.getRootPath());
		
		Path root = ruleService.getStorageTransformationPath();
		
		Path filePath = ruleService.filePath(rdt);
		if (!filePath.toFile().exists() || !filePath.toFile().isFile()) {
			errors.rejectValue("rootPath", "rule.path.not_accessible");
		}
		
		if (!filePath.normalize().startsWith(root.normalize())) {
			errors.rejectValue("rootPath", "rule.path.not_nested");
		}
		
	}

}
