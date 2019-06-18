package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.service.content.document.SendDocumentDelisWebApiService;
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
@RequestMapping("/rest/document/send")
public class SendDocumentRestController {

    private final SendDocumentDelisWebApiService sendDocumentDelisWebApiService;
    private final DownloadService downloadService;

    public SendDocumentRestController(SendDocumentDelisWebApiService sendDocumentDelisWebApiService, DownloadService downloadService) {
        this.sendDocumentDelisWebApiService = sendDocumentDelisWebApiService;
        this.downloadService = downloadService;
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
        return downloadService.downloadFile(data, fileName);
    }
}
