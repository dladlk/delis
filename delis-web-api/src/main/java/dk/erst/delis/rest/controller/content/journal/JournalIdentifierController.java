package dk.erst.delis.rest.controller.content.journal;

import dk.erst.delis.service.content.journal.identifier.JournalIdentifierDelisWebApiService;

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
@RequestMapping("/rest/journal/identifier")
public class JournalIdentifierController {

    private final JournalIdentifierDelisWebApiService journalIdentifierDelisWebApiService;

    @Autowired
    public JournalIdentifierController(JournalIdentifierDelisWebApiService journalIdentifierDelisWebApiService) {
        this.journalIdentifierDelisWebApiService = journalIdentifierDelisWebApiService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(journalIdentifierDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/one/{identifierId}")
    public ResponseEntity getByIdentifier(@PathVariable @Min(1) long identifierId, WebRequest webRequest) {
        return ResponseEntity.ok(journalIdentifierDelisWebApiService.getByIdentifier(webRequest, identifierId));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalIdentifierDelisWebApiService.getOneById(id));
    }
}
