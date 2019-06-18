package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import dk.erst.delis.service.inner.DownloadService;
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
@RequestMapping("/rest/document")
public class DocumentRestController {

    private final DocumentDelisWebApiService documentDelisWebApiService;
    private final DownloadService downloadService;

    public DocumentRestController(DocumentDelisWebApiService documentDelisWebApiService, DownloadService downloadService) {
        this.documentDelisWebApiService = documentDelisWebApiService;
        this.downloadService = downloadService;
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
        return downloadService.downloadFile(data, fileName);
    }
}
