package dk.erst.delis.web.validationrule;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.task.document.process.RuleService;

@Controller
@RequestMapping("/validationrule")
public class ValidationRuleController {

    private ValidationRuleService service;
    private RuleService ruleService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
    	StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, stringtrimmer);    	
        binder.addValidators(new ValidationRuleDataValidator(ruleService));
    }    
    
    @Autowired
    public ValidationRuleController(ValidationRuleService service, RuleService ruleService) {
        this.service = service;
        this.ruleService = ruleService;
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("validationRule") RuleDocumentValidationData ruleData, BindingResult bindingResult, Model model, RedirectAttributes ra) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some fields are not valid.");
			fillModel(model);
			return "validationrule/edit";
		}
		
		boolean isNew = ruleData.getId() == null;
		
        service.saveRule(ruleData);
        
		if (isNew) {
			ra.addFlashAttribute("message", "Rule is created.");
		} else {
			ra.addFlashAttribute("message", "Rule is updated.");
		}
        return "redirect:/setup/index";
    }

    @GetMapping("create/{id}")
    public String createNew(@PathVariable(required = false) Long id, Model model) {
        RuleDocumentValidationData rule = new RuleDocumentValidationData();
		if (id == null || id == 0) {
			rule.setPriority(10);
        } else {
            RuleDocumentValidation validationRule = service.findById(id);
            BeanUtils.copyProperties(validationRule, rule);
        }
		model.addAttribute("validationRule", rule);
        fillModel(model);
        return "validationrule/edit";
    }

	public void fillModel(Model model) {
		model.addAttribute("documentFormatList", Arrays.stream(DocumentFormat.values()).filter(d -> !d.isUnsupported()).sorted((a, b) -> a.getCode().compareTo(b.getCode())).collect(Collectors.toList()));
        model.addAttribute("validationTypeList", RuleDocumentValidationType.values());
	}

    @GetMapping("update/{id}")
    public String updateRule(@PathVariable long id, Model model) {
        RuleDocumentValidation validationRule = service.findById(id);
        RuleDocumentValidationData validationRuleData = new RuleDocumentValidationData();
        BeanUtils.copyProperties(validationRule, validationRuleData);
        model.addAttribute("validationRule", validationRuleData);
        fillModel(model);
        return "validationrule/edit";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable long id, Model model, RedirectAttributes ra) {
        service.deleteRule(id);
        
        ra.addFlashAttribute("message", "Rule is deleted.");
        
        return "redirect:/setup/index";
    }

    @GetMapping("createdefault")
    public String createDefault(RedirectAttributes ra) {
        service.recreateDefault();
        
        ra.addFlashAttribute("message", "Default validation rules are recreated.");
        
        return "redirect:/setup/index";
    }

    @GetMapping("flushcache")
    public String flushCache(RedirectAttributes ra) {
        ruleService.refreshValidationList();
        ra.addFlashAttribute("message", "Validation rule cache is flushed.");
        return "redirect:/setup/index";
    }
}
