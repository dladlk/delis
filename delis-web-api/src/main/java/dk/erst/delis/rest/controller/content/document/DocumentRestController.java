package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    private final DocumentDelisWebApiService documentDelisWebApiService;

    @Autowired
    public DocumentRestController(DocumentDelisWebApiService documentDelisWebApiService) {
        this.documentDelisWebApiService = documentDelisWebApiService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(documentDelisWebApiService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(documentDelisWebApiService.getOneById(id));
    }

    @GetMapping("/{id}/bytes")
    public ResponseEntity findListDocumentBytesByDocumentId(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(documentDelisWebApiService.findListDocumentBytesByDocumentId(id));
    }

    @GetMapping("/download/{id}/bytes/{bytesId}")
    public ResponseEntity<Object> downloadFile(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long bytesId) {
        byte[] data = documentDelisWebApiService.downloadFile(id, bytesId);
        String fileName = "data_" + id + "_" + bytesId + ".xml";
        BodyBuilder resp = ResponseEntity.ok();
        resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        resp.contentType(MediaType.parseMediaType("application/octet-stream"));
        return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
    }
}
