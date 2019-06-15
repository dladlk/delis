package dk.erst.delis.web.transformationrule;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.task.document.process.RuleService;
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
@RequestMapping("/transformationrule")
public class TransformationRuleController {

    private TransformationRuleService service;
    private RuleService ruleService;

    @Autowired
    public TransformationRuleController(TransformationRuleService service, RuleService ruleService) {
        this.service = service;
        this.ruleService = ruleService;
    }

    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("transformationRuleList", service.loadRulesList());
        return "setup/index";
    }

    @PostMapping("save")
    public String createNew(@Valid RuleDocumentTransformationData ruleData, Model model) throws Exception {
        service.saveRule(ruleData);
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
        model.addAttribute("documentFormatList", DocumentFormatFamily.values());
        return "transformationrule/edit";
    }

    @GetMapping("createdefault")
    public String createDefault(Model model) {
        service.recreateDefault();
        return "redirect:/setup/index";
    }

    @GetMapping("update/{id}")
    public String updateAccessPoint(@PathVariable long id, Model model) {
        RuleDocumentTransformation transformationRule = service.findById(id);
        RuleDocumentTransformationData transformationRuleData = new RuleDocumentTransformationData();
        BeanUtils.copyProperties(transformationRule, transformationRuleData);
        model.addAttribute("transformationRule", transformationRuleData);
        model.addAttribute("documentFormatList", DocumentFormatFamily.values());
        return "transformationrule/edit";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable long id, Model model) {
        service.deleteRule(id);
        return "redirect:/setup/index";
    }

    @GetMapping("flushcache")
    public String flushCache(Model model) {
        ruleService.refreshTransformationList();
        return "redirect:/setup/index";
    }
}
