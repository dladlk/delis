package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.SendDocumentDelisWebApiService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;

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

    @GetMapping("/{id}/bytes")
    public ResponseEntity findListSendDocumentBytesBySendDocumentId(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(sendDocumentDelisWebApiService.findListSendDocumentBytesBySendDocumentId(id));
    }

    @GetMapping("/{id}/journal")
    public ResponseEntity findListJournalSendDocumentBySendDocumentId(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(sendDocumentDelisWebApiService.findListJournalSendDocumentBySendDocumentId(id));
    }

    @GetMapping("/download/{id}/bytes/{bytesId}")
    public ResponseEntity<Object> downloadFile(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long bytesId) {
        byte[] data = sendDocumentDelisWebApiService.downloadFile(id, bytesId);
        String fileName = "data_" + id + "_" + bytesId + ".xml";
        BodyBuilder resp = ResponseEntity.ok();
        resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        resp.contentType(MediaType.parseMediaType("application/octet-stream"));
        return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
    }
}
