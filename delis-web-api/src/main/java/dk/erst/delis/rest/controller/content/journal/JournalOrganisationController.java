package dk.erst.delis.rest.controller.content.journal;

import dk.erst.delis.service.content.journal.organisation.JournalOrganisationDelisWebApiService;

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
@RequestMapping("/rest/journal/organisation")
public class JournalOrganisationController {

    private final JournalOrganisationDelisWebApiService journalOrganisationDelisWebApiService;

    @Autowired
    public JournalOrganisationController(JournalOrganisationDelisWebApiService journalOrganisationDelisWebApiService) {
        this.journalOrganisationDelisWebApiService = journalOrganisationDelisWebApiService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(journalOrganisationDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalOrganisationDelisWebApiService.getOneById(id));
    }
}
