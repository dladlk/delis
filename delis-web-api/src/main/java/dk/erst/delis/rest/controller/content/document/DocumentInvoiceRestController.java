package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import dk.erst.delis.service.inner.DownloadService;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.File;

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentInvoiceRestController {

    private final DocumentDelisWebApiService documentDelisWebApiService;
    private final DownloadService downloadService;

    public DocumentInvoiceRestController(DocumentDelisWebApiService documentDelisWebApiService, DownloadService downloadService) {
        this.documentDelisWebApiService = documentDelisWebApiService;
        this.downloadService = downloadService;
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<DataContainer<InvoiceResponseData>> view(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(new DataContainer<>(documentDelisWebApiService.getInvoiceResponseDataByDocument(id)));
    }

    @PostMapping("/invoice/generate")
    public ResponseEntity<Object> generateInvoiceResponse(@RequestBody InvoiceResponseForm invoiceResponseForm) {
        Object o = documentDelisWebApiService.generateInvoiceResponse(invoiceResponseForm);
        if (o instanceof File) {
            File file = (File) o;
            String fileName = invoiceResponseForm.getDocumentFormatName() + ".xml";
            return downloadService.downloadFile(file, fileName);
        } else {
            return ResponseEntity.ok(new DataContainer<>(o));
        }
    }
}
