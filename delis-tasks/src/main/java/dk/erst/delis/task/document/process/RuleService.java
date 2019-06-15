package dk.erst.delis.task.document.process;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RuleService {

	private List<RuleDocumentTransformation> transformationList;
	private List<RuleDocumentValidation> validationList;
	private ConfigBean configBean;
	private ValidationRuleService validationRuleService;
	private TransformationRuleService transformationRuleService;

	@Autowired
	public RuleService(ConfigBean configBean, ValidationRuleService validationRuleService, TransformationRuleService transformationRuleService) {
		this.configBean = configBean;
		this.validationRuleService = validationRuleService;
		this.transformationRuleService = transformationRuleService;
		this.validationList = buildValidationRuleList();
		this.transformationList = buildTransformationRuleList();
	}
	
	private List<RuleDocumentValidation> buildValidationRuleList() {
		List<RuleDocumentValidation> l = new ArrayList<>();

		Iterable<RuleDocumentValidation> all = validationRuleService.findAll();
		all.iterator().forEachRemaining(l::add);

		return l;
	}

	private List<RuleDocumentTransformation> buildTransformationRuleList() {
		List<RuleDocumentTransformation> l = new ArrayList<>();

		Iterable<RuleDocumentTransformation> all = transformationRuleService.findAll();
		all.iterator().forEachRemaining(l::add);

		return l;
	}

	public List<RuleDocumentValidation> getValidationList() {
		return this.validationList;
	}

	public void refreshValidationList () {
		validationList = buildValidationRuleList();
	}

	public void refreshTransformationList () {
		transformationList = buildTransformationRuleList();
	}

	public List<RuleDocumentValidation> getValidationRuleListByFormat(DocumentFormat format) {
		return this.validationList.stream()

				.filter(r -> r.getDocumentFormat() == format)

				.sorted(new Comparator<RuleDocumentValidation>() {
					@Override
					public int compare(RuleDocumentValidation o1, RuleDocumentValidation o2) {
						if (o1.getValidationType() != o2.getValidationType()) {
							return o1.getValidationType().compareTo(o2.getValidationType());
						}
						return Integer.compare(o1.getPriority(), o2.getPriority());
					}
				})

				.collect(Collectors.toList());
	}

	public List<RuleDocumentTransformation> getTransformationList() {
		return transformationList;
	}

	public RuleDocumentTransformation getTransformation(DocumentFormatFamily format) {
		Optional<RuleDocumentTransformation> findFirst = transformationList

				.stream()

				.filter(r -> r.getDocumentFormatFamilyFrom() == format)

				.findFirst();

		return findFirst.orElse(null);
	}
	
	public Path filePath(RuleDocumentValidation r) {
		Path path = configBean.getStorageValidationPath().resolve(r.getRootPath());
		log.debug("Built validation path " + path);
		return path;
	}

	public Path filePath(RuleDocumentTransformation r) {
		Path path = configBean.getStorageTransformationPath().resolve(r.getRootPath());
		log.debug("Built transformation path " + path);
		return path;
	}
}
