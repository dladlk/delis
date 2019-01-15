package dk.erst.delis.web.validationrule;

import dk.erst.delis.data.DocumentFormatFamily;
import dk.erst.delis.data.RuleDocumentValidation;
import dk.erst.delis.data.RuleDocumentValidationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/validationrule")
@Slf4j
public class ValidationRuleController {

    private ValidationRuleService service;

    @Autowired
    public ValidationRuleController(ValidationRuleService service) {
        this.service = service;
    }

    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("validationRuleList", service.loadRulesList());
        return "validationrule/list";
    }

    @PostMapping("save")
    public String createNew(@Valid RuleDocumentValidationData ruleData, Model model) throws Exception {
        service.saveRule(ruleData);
        return "redirect:/validationrule/list";
    }

    @GetMapping("create/{id}")
    public String createNew(@PathVariable(required = false) Long id, Model model) {
        if (id == null || id == 0) {
            model.addAttribute("validationRule", new RuleDocumentValidationData());
        } else {
            RuleDocumentValidation validationRule = service.findById(id);
            RuleDocumentValidationData validationRuleData = new RuleDocumentValidationData();
            BeanUtils.copyProperties(validationRule, validationRuleData);
            model.addAttribute("validationRule", validationRuleData);
        }
        model.addAttribute("documentFormatList", DocumentFormatFamily.values());
        model.addAttribute("validationTypeList", RuleDocumentValidationType.values());
        return "validationrule/edit";
    }

    @GetMapping("update/{id}")
    public String updateAccessPoint(@PathVariable long id, Model model) {
        RuleDocumentValidation validationRule = service.findById(id);
        RuleDocumentValidationData validationRuleData = new RuleDocumentValidationData();
        BeanUtils.copyProperties(validationRule, validationRuleData);
        model.addAttribute("validationRule", validationRuleData);
        model.addAttribute("documentFormatList", DocumentFormatFamily.values());
        model.addAttribute("validationTypeList", RuleDocumentValidationType.values());
        return "validationrule/edit";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable long id, Model model) {
        service.deleteRule(id);
        model.addAttribute("validationRuleList", service.loadRulesList());
        return "validationrule/list";
    }
}
