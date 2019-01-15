package dk.erst.delis.web.setup;

import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.dao.ConfigValueDaoRepository;

@Controller
public class SetupController {

	@Autowired
	private ConfigBean configBean;
	
	@Autowired
	private ConfigProperties configProperties;
	
	@Autowired
	private ConfigValueDaoRepository configValueDaoRepository;
	
	@GetMapping("/setup/index")
	public String index(Model model) {
		model.addAttribute("configBean", configBean);
		model.addAttribute("configProperties", configProperties);
		model.addAttribute("configList", configValueDaoRepository.findAll(Sort.by("configValueType")));
		return "/setup/index";
	}
	
	@GetMapping("/setup/config/edit/{id}")
	public String edit(Model model, @PathVariable long id) {
		model.addAttribute("configValue", configValueDaoRepository.findById(id));
		model.addAttribute("configValueTypeList", ConfigValueType.values());
		return "/setup/config_value_edit";
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
		
		configValueDaoRepository.save(configValue);
		
		return "redirect:/setup/index";
	}
	
}
