package dk.erst.delis.web.transformationrule;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.task.document.process.RuleService;

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

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Controller
@RequestMapping("/transformationrule")
public class TransformationRuleController {

    private TransformationRuleService service;
    private RuleService ruleService;
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
    	StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, stringtrimmer);    	
        binder.addValidators(new TransformationRuleDataValidator(ruleService));
    }    

    @Autowired
    public TransformationRuleController(TransformationRuleService service, RuleService ruleService) {
        this.service = service;
        this.ruleService = ruleService;
    }

	private void fillModel(Model model) {
		model.addAttribute("documentFormatList", Arrays.stream(DocumentFormatFamily.values()).filter(d -> d.isTransformable()).collect(Collectors.toList()));
	}

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("transformationRule") RuleDocumentTransformationData ruleData, BindingResult bindingResult, Model model, RedirectAttributes ra) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some fields are not valid.");
			fillModel(model);
			return "transformationrule/edit";
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
        if (id == null || id == 0) {
            model.addAttribute("transformationRule", new RuleDocumentTransformationData());
        } else {
            RuleDocumentTransformation transformationRule = service.findById(id);
            RuleDocumentTransformationData transformationRuleData = new RuleDocumentTransformationData();
            BeanUtils.copyProperties(transformationRule, transformationRuleData);
            model.addAttribute("transformationRule", transformationRuleData);
        }
        fillModel(model);
        return "transformationrule/edit";
    }

    @GetMapping("createdefault")
    public String createDefault(RedirectAttributes ra) {
        service.recreateDefault();
        
        ra.addFlashAttribute("message", "Default transformation rules are recreated.");
        
        return "redirect:/setup/index";
    }

    @GetMapping("update/{id}")
    public String updateAccessPoint(@PathVariable long id, Model model) {
        RuleDocumentTransformation transformationRule = service.findById(id);
        RuleDocumentTransformationData transformationRuleData = new RuleDocumentTransformationData();
        BeanUtils.copyProperties(transformationRule, transformationRuleData);
        model.addAttribute("transformationRule", transformationRuleData);
        fillModel(model);
        return "transformationrule/edit";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable long id, Model model, RedirectAttributes ra) {
        service.deleteRule(id);
        
        ra.addFlashAttribute("message", "Rule is deleted.");
        
        return "redirect:/setup/index";
    }

    @GetMapping("flushcache")
    public String flushCache(RedirectAttributes ra) {
        ruleService.refreshTransformationList();
        ra.addFlashAttribute("message", "Transformation rule cache is flushed.");
        return "redirect:/setup/index";
    }
}
