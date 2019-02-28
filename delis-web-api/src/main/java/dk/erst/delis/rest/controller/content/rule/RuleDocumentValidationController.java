package dk.erst.delis.rest.controller.content.rule;

import dk.erst.delis.service.content.rule.validation.RuleDocumentValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

/**
 * @author funtusthan, created by 15.01.19
 */

@Validated
@RestController
@RequestMapping("/rest/rule/document/validation")
public class RuleDocumentValidationController {

    private final RuleDocumentValidationService ruleDocumentValidationService;

    @Autowired
    public RuleDocumentValidationController(RuleDocumentValidationService ruleDocumentValidationService) {
        this.ruleDocumentValidationService = ruleDocumentValidationService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(ruleDocumentValidationService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(ruleDocumentValidationService.getOneById(id));
    }
}
