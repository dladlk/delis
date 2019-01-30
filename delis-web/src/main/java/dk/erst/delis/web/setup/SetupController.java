package dk.erst.delis.web.setup;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SetupController {

	@Autowired
	private ConfigBean configBean;
	
	@Autowired
	ValidationRuleService validationRuleService;

	@Autowired
	SetupService setupService;

	@Autowired
	TransformationRuleService transformationRuleService;

	@GetMapping("/setup/index")
	public String index(Model model) {
		model.addAttribute("configValuesList", setupService.createConfigValuesList(configBean));
		model.addAttribute("configBean", configBean);
		model.addAttribute("configList", setupService.getAllTypesFromDB());
		model.addAttribute("validationRuleList", validationRuleService.loadRulesList());
		model.addAttribute("transformationRuleList", transformationRuleService.loadRulesList());
		return "/setup/index";
	}

//	@GetMapping("/setup/config/edit/{id}")
//	public String edit(Model model, @PathVariable long id) {
//		model.addAttribute("configValue", setupService.findById(id));
//		model.addAttribute("configValueTypeList", ConfigValueType.values());
//		return "/setup/config_value_edit";
//	}
	
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
