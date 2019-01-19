package dk.erst.delis.rest.controller.content.journal;

import dk.erst.delis.service.content.journal.organisation.JournalOrganisationService;

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
 * @author funtusthan, created by 14.01.19
 */

@Slf4j
@RestController
@RequestMapping("/rest/journal/organisation")
public class JournalOrganisationController {

    private final JournalOrganisationService journalOrganisationService;

    @Autowired
    public JournalOrganisationController(JournalOrganisationService journalOrganisationService) {
        this.journalOrganisationService = journalOrganisationService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(journalOrganisationService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalOrganisationService.getOneById(id));
    }
}
