package dk.erst.delis.rest.controller.rule;

import dk.erst.delis.service.rule.transformation.RuleDocumentTransformationService;

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
@RequestMapping("/rest/rule/document/transformation")
public class RuleDocumentTransformationController {

    private final RuleDocumentTransformationService ruleDocumentTransformationService;

    @Autowired
    public RuleDocumentTransformationController(RuleDocumentTransformationService ruleDocumentTransformationService) {
        this.ruleDocumentTransformationService = ruleDocumentTransformationService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(ruleDocumentTransformationService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(ruleDocumentTransformationService.getOneById(id));
    }
}
