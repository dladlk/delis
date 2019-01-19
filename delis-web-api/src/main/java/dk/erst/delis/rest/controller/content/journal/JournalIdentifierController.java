package dk.erst.delis.rest.controller.content.journal;

import dk.erst.delis.service.content.journal.identifier.JournalIdentifierService;

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
@RequestMapping("/rest/journal/identifier")
public class JournalIdentifierController {

    private final JournalIdentifierService journalIdentifierService;

    @Autowired
    public JournalIdentifierController(JournalIdentifierService journalIdentifierService) {
        this.journalIdentifierService = journalIdentifierService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(journalIdentifierService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalIdentifierService.getOneById(id));
    }
}
