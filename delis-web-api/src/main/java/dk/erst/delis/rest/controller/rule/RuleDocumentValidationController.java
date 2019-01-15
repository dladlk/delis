package dk.erst.delis.rest.controller.rule;

import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.service.rule.validation.RuleDocumentValidationService;
import dk.erst.delis.util.WebRequestUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

/**
 * @author funtusthan, created by 15.01.19
 */

@Slf4j
@RestController
@RequestMapping("/rest/rule/document/validation")
public class RuleDocumentValidationController {

    private final RuleDocumentValidationService ruleDocumentValidationService;

    @Autowired
    public RuleDocumentValidationController(RuleDocumentValidationService ruleDocumentValidationService) {
        this.ruleDocumentValidationService = ruleDocumentValidationService;
    }

    @GetMapping
    public ResponseEntity getRuleDocumentValidationList(WebRequest webRequest) {
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(webRequest);
        return ResponseEntity.ok(ruleDocumentValidationService.getAllAfterFilteringAndSorting(pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getRuleDocumentValidationById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(ruleDocumentValidationService.getOneById(id));
    }
}
