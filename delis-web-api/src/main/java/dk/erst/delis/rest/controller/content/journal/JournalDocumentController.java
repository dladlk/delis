package dk.erst.delis.rest.controller.content.journal;

import dk.erst.delis.service.content.journal.document.JournalDocumentDelisWebApiService;

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
 * @author funtusthan, created by 13.01.19
 */

@Validated
@RestController
@RequestMapping("/rest/journal/document")
public class JournalDocumentController {

    private final JournalDocumentDelisWebApiService journalDocumentDelisWebApiService;

    @Autowired
    public JournalDocumentController(JournalDocumentDelisWebApiService journalDocumentDelisWebApiService) {
        this.journalDocumentDelisWebApiService = journalDocumentDelisWebApiService;
    }

    @GetMapping()
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(journalDocumentDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/one/{documentId}")
    public ResponseEntity getByDocument(@PathVariable @Min(1) long documentId, WebRequest webRequest) {
        return ResponseEntity.ok(journalDocumentDelisWebApiService.getByDocument(webRequest, documentId));
    }

    @GetMapping("/one/error/{documentId}")
    public ResponseEntity getByJournalDocumentDocumentId(@PathVariable @Min(1) long documentId) {
        return ResponseEntity.ok(journalDocumentDelisWebApiService.getByJournalDocumentDocumentId(documentId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalDocumentDelisWebApiService.getOneById(id));
    }
}
