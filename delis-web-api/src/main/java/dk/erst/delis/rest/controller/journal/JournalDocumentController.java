package dk.erst.delis.rest.controller.journal;

import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.service.journal.document.JournalDocumentService;
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
 * @author funtusthan, created by 13.01.19
 */

@Slf4j
@RestController
@RequestMapping("/rest/journal/document")
public class JournalDocumentController {

    private final JournalDocumentService journalDocumentService;

    @Autowired
    public JournalDocumentController(JournalDocumentService journalDocumentService) {
        this.journalDocumentService = journalDocumentService;
    }

    @GetMapping
    public ResponseEntity getJournalDocumentList(WebRequest webRequest) {
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(webRequest);
        return ResponseEntity.ok(journalDocumentService.getAllAfterFilteringAndSorting(pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getJournalDocumentById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(journalDocumentService.getOneById(id));
    }
}
