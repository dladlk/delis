package dk.erst.delis.rest.controller.identifier;

import dk.erst.delis.service.identifier.IdentifierGroupService;

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
 * @author funtusthan, created by 17.01.19
 */

@Validated
@RestController
@RequestMapping("/rest/identifier/group")
public class IdentifierGroupController {

    private final IdentifierGroupService identifierGroupService;

    @Autowired
    public IdentifierGroupController(IdentifierGroupService identifierGroupService) {
        this.identifierGroupService = identifierGroupService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(identifierGroupService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(identifierGroupService.getOneById(id));
    }
}
