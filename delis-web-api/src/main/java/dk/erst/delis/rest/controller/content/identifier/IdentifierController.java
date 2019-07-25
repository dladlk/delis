package dk.erst.delis.rest.controller.content.identifier;

import dk.erst.delis.service.content.identifier.IdentifierDelisWebApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/rest/identifier")
public class IdentifierController {

    private final IdentifierDelisWebApiService identifierDelisWebApiService;

    @Autowired
    public IdentifierController(IdentifierDelisWebApiService identifierDelisWebApiService) {
        this.identifierDelisWebApiService = identifierDelisWebApiService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(identifierDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(identifierDelisWebApiService.getOneById(id));
    }
}
