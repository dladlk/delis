package dk.erst.delis.web.setup;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.entities.rule.IRuleDocument;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.config.ConfigValueType;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import lombok.Getter;

@Controller
public class SetupController {

	@Autowired
	private ConfigBean configBean;
	
	@Autowired
	private ValidationRuleService validationRuleService;

	@Autowired
	private SetupService setupService;

	@Autowired
	private TransformationRuleService transformationRuleService;
	
	@Autowired
	private RuleService ruleService;

	@GetMapping("/setup/index")
	public String index(Model model) {
		model.addAttribute("configValuesList", setupService.createConfigValuesList(configBean));
		model.addAttribute("configBean", configBean);
		model.addAttribute("configList", setupService.getAllTypesFromDB());

		List<RuleDocumentValidation> valRules = validationRuleService.loadForSetup();
		List<RuleDocumentTransformation> transRules = transformationRuleService.loadForSetup();
		model.addAttribute("validationRuleList", valRules);
		model.addAttribute("transformationRuleList", transRules);
		
		model.addAttribute("validationChange", buildChangedRuleMap(valRules, ruleService.getValidationList()));
		model.addAttribute("transformationChange", buildChangedRuleMap(transRules, ruleService.getTransformationList()));
		
		return "/setup/index";
	}
	
	@Getter
	private static class ChangeRuleData {
		private Set<Long> idSet = new HashSet<Long>();
		private boolean someRulesDeleted;
	}
	
	private static <T extends IRuleDocument> ChangeRuleData buildChangedRuleMap(List<T> rules, List<T> cachedList) {
		ChangeRuleData res = new ChangeRuleData();
		Set<Long> set = res.idSet;
		
		Set<Long> dbRuleAllIdSet = new HashSet<Long>();
		for (T dbRule : rules) {
			dbRuleAllIdSet.add(dbRule.getId());
			boolean noChangeByCache = false;
			Optional<T> byId = cachedList.stream().filter(s -> s.getId().equals(dbRule.getId())).findAny();
			if (byId.isPresent()) {
				T cachedRule = byId.get();
				if (cachedRule.isEqualData(dbRule)) {
					noChangeByCache = true;
				}
			} else if (!dbRule.isActive()) {
				/*
				 * Cache contains only active rules, so if db rule is absent in cache but is inactive - we consider as no change in cache
				 */
				noChangeByCache = true;
			}
			if (!noChangeByCache) {
				set.add(dbRule.getId());
			}
		}
		
		cachedList.forEach(c -> {
			if (!dbRuleAllIdSet.contains(c.getId())) {
				res.someRulesDeleted = true;
			}
		});
		
		return res;
	}


	@GetMapping("/setup/config/create")
	public String create(Model model) {
		model.addAttribute("configValue", new ConfigValue());
		model.addAttribute("configValueTypeList", ConfigValueType.values());
		return "/setup/config_value_edit";
	}
	
	@PostMapping("/setup/config/save")
	public String save(@ModelAttribute ConfigValue configValue, Model model) {
		if (StringUtils.isEmpty(configValue.getValue()) || configValue.getConfigValueType() == null) {
			model.addAttribute("errorMessage", "Type and value are mandatory");
			return "/setup/config_value_edit";
		}

		setupService.save(configValue);
		configBean.refresh();

		return "redirect:/setup/index";
	}

	@GetMapping("/setup/config/dbupdate")
	public String dbUpdate(Model model) {
		configBean.refresh();
		return "redirect:/setup/index";
	}

	@GetMapping("/setup/config/edit/{typeName}")
	public String edit(@PathVariable String typeName, Model model) {

		ConfigValue configValueForType = setupService.getConfigValueForType(typeName);
		model.addAttribute("configValue", configValueForType);

		return "/setup/config_value_edit";
	}
}
