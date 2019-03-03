package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.DocumentService;

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
 * @author Iehor Funtusov, created by 21.12.18
 */

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    private final DocumentService documentService;

    @Autowired
    public DocumentRestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(documentService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(documentService.getOneById(id));
    }

    @GetMapping("/{id}/bytes")
    public ResponseEntity findListDocumentBytesByDocumentId(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(documentService.findListDocumentBytesByDocumentId(id));
    }
}
