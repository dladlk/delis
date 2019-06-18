package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.SendDocumentDelisWebApiService;
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
@RequestMapping("/rest/document/send")
public class SendDocumentRestController {

    private final SendDocumentDelisWebApiService sendDocumentDelisWebApiService;

    public SendDocumentRestController(SendDocumentDelisWebApiService sendDocumentDelisWebApiService) {
        this.sendDocumentDelisWebApiService = sendDocumentDelisWebApiService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(sendDocumentDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(sendDocumentDelisWebApiService.getOneById(id));
    }
}
