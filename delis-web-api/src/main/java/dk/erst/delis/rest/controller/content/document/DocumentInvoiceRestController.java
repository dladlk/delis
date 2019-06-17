package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentInvoiceRestController {

    private final DocumentDelisWebApiService documentDelisWebApiService;

    public DocumentInvoiceRestController(DocumentDelisWebApiService documentDelisWebApiService) {
        this.documentDelisWebApiService = documentDelisWebApiService;
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
            ResponseEntity.BodyBuilder resp = ResponseEntity.ok();
            resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + invoiceResponseForm.getDocumentFormatName() + ".xml\"");
            resp.contentType(MediaType.parseMediaType("application/octet-stream"));
            try {
                return resp.body(new InputStreamResource(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                log.error("Failed to generate without sending", e);
                throw new RestConflictException(Collections.singletonList(
                        new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to generate without sending: " + e.getMessage())));
            }
        } else {
            return ResponseEntity.ok(new DataContainer<>(o));
        }
    }
}
