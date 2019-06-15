package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.rest.data.request.document.UpdateDocumentStatusData;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.SuccessData;
import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import dk.erst.delis.web.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    private final DocumentDelisWebApiService documentDelisWebApiService;
    private final DocumentService documentService;

    @Autowired
    public DocumentRestController(DocumentDelisWebApiService documentDelisWebApiService, DocumentService documentService) {
        this.documentDelisWebApiService = documentDelisWebApiService;
        this.documentService = documentService;
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

    @PostMapping("/update-status")
    public ResponseEntity<DataContainer<SuccessData>> updateDocumentStatus(@RequestBody UpdateDocumentStatusData data) {
        List<Long> ids = data.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "you have not selected any document")));
        }
        if (data.getStatus() == null) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "you have not selected any document status")));
        }
        documentService.updateStatuses(ids, data.getStatus());
        return ResponseEntity.ok(new DataContainer<>(new SuccessData()));
    }

    @GetMapping("/download/{id}/bytes/{bytesId}")
    public ResponseEntity<Object> downloadFile(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long bytesId) {
        DocumentBytes documentBytes = documentDelisWebApiService.findByIdAndDocumentId(id, bytesId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.documentService.getDocumentBytesContents(documentBytes, out);
        byte[] data = out.toByteArray();
        ResponseEntity.BodyBuilder resp = ResponseEntity.ok();
        resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data_" + id + "_" + bytesId + ".xml\"");
        resp.contentType(MediaType.parseMediaType("application/octet-stream"));
        return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
    }
}
