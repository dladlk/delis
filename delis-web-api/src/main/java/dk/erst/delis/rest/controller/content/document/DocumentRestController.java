package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.rest.data.request.document.UpdateDocumentStatusData;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.SuccessData;
import dk.erst.delis.service.content.document.DocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.nio.file.Path;

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

    @PostMapping("/update-status")
    public ResponseEntity<DataContainer<SuccessData>> updateDocumentStatus(@RequestBody UpdateDocumentStatusData data) {
        documentService.updateDocumentStatus(data);
        return ResponseEntity.ok(new DataContainer<>(new SuccessData()));
    }

    @GetMapping("/download/{id}/bytes/{bytesId}")
    public Resource downloadFile(@PathVariable Long id, @PathVariable Long bytesId, HttpServletResponse response) {
        Path path = documentService.downloadFile(id, bytesId);
        Resource resource = new FileSystemResource(path.toAbsolutePath().toString());
        String filename = "data_" + id + "_" + bytesId + ".xml";
        response.setContentType("text/xml; charset=utf-8");
//        response.setContentType("application/xml; charset=utf-8");
        response.setContentType(MediaType.parseMediaType("application/octet-stream").getType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        response.setHeader("filename", filename);
        return resource;
    }
}
