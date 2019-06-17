package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.SuccessData;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.ApplicationResponseService;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.SendDocumentService;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;
import dk.erst.delis.web.document.ir.InvoiceResponseFormControllerConst;
import dk.erst.delis.web.document.ir.MessageLevelResponseConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentInvoiceRestController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private SendDocumentService sendDocumentService;
    @Autowired
    private ApplicationResponseService applicationResponseService;

    @GetMapping("/{id}/invoice")
    public ResponseEntity<DataContainer<InvoiceResponseData>> view(@PathVariable @Min(1) long id) {
        Document document = documentService.getDocument(id);
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document is not found")));
        }

        InvoiceResponseData data = new InvoiceResponseData();
//        data.setDocument(document);
//        data.setDocumentStatusList(Arrays.asList(DocumentStatus.values()));
//        data.setLastJournalList(documentService.getDocumentRecords(document));
//        data.setErrorListByJournalDocumentIdMap(documentService.getErrorListByJournalDocumentIdMap(document));
//        data.setDocumentBytes(documentDelisWebApiService.findListDocumentBytesByDocumentId(document.getId()).getItems());

        data.setInvoiceResponseUseCaseList(InvoiceResponseFormControllerConst.useCaseList);
        data.setInvoiceStatusCodeList(InvoiceResponseFormControllerConst.invoiceStatusCodeList);
        data.setStatusActionList(InvoiceResponseFormControllerConst.statusActionList);
        data.setStatusReasonList(InvoiceResponseFormControllerConst.statusReasonList);

        data.setMessageLevelResponseUseCaseList(MessageLevelResponseConst.useCaseList);
        data.setApplicationResponseTypeCodeList(MessageLevelResponseConst.applicationResponseTypeCodeList);
        data.setApplicationResponseLineResponseCodeList(MessageLevelResponseConst.applicationResponseLineResponseCodeList);
        data.setApplicationResponseLineReasonCodeList(MessageLevelResponseConst.applicationResponseLineReasonCodeList);


//        applicationResponseFormController.fillModel(model, document);

        return ResponseEntity.ok(new DataContainer<>(data));
    }

    @PostMapping("/invoice/generate")
    public ResponseEntity<Object> generateInvoiceResponse(@RequestBody InvoiceResponseForm invoiceResponseForm) {
        log.info("Generating ApplicationResponse for " + invoiceResponseForm);
        Document document = documentService.getDocument(invoiceResponseForm.getDocumentId());
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document is not found")));
        }

        boolean success = false;
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("Generated" + invoiceResponseForm.getDocumentFormatName() + "_", ".xml").toFile();
        } catch (IOException e) {
            log.error("Failed document generated", e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed document generated: " + e.getMessage())));
        }
        try (OutputStream out = new FileOutputStream(tempFile)) {
            success = applicationResponseService.generateApplicationResponse(document, invoiceResponseForm.getData(), out);
        } catch (ApplicationResponseService.ApplicationResponseGenerationException | IOException e) {
            log.error("Failed document generated", e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed document generated: " + e.getMessage())));
        }

        if (!success) {
            log.error("Failed to generate " + invoiceResponseForm.getDocumentFormatName());
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to generate " + invoiceResponseForm.getDocumentFormatName())));
        }

        if (invoiceResponseForm.isValidate()) {
            List<ErrorRecord> errorList;
            if (invoiceResponseForm.isMessageLevelResponse()) {
                errorList = applicationResponseService.validateMessageLevelResponse(tempFile.toPath());
            } else {
                errorList = applicationResponseService.validateInvoiceResponse(tempFile.toPath());
            }
            if (!errorList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Generated ");
                sb.append(invoiceResponseForm.getDocumentFormatName());
                sb.append(" is not valid by schema or schematron, found ");
                sb.append(errorList.size());
                sb.append(" errors");
                log.error(sb.toString());
                throw new RestConflictException(Collections.singletonList(
                        new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), sb.toString())));
            }
        }

        if (invoiceResponseForm.isGenerateWithoutSending()) {
            ResponseEntity.BodyBuilder resp = ResponseEntity.ok();
            resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + invoiceResponseForm.getDocumentFormatName() + ".xml\"");
            resp.contentType(MediaType.parseMediaType("application/octet-stream"));
            try {
                return resp.body(new InputStreamResource(new FileInputStream(tempFile)));
            } catch (FileNotFoundException e) {
                log.error("Failed to generate without sending", e);
                throw new RestConflictException(Collections.singletonList(
                        new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to generate without sending: " + e.getMessage())));
            }
        }

        try {
            SendDocument sendDocument = sendDocumentService.sendFile(tempFile.toPath(), "Generated by form on document #" + invoiceResponseForm.getDocumentId(), !invoiceResponseForm.isValidate());
            return ResponseEntity.ok(new DataContainer<>(new SuccessData(true, invoiceResponseForm.getDocumentFormatName() + " with status " + sendDocument.getDocumentStatus())));
        } catch (DocumentProcessStepException se) {
            log.error("Failed document processing", se);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to process file " + tempFile + " with error " + se.getMessage())));
        } catch (Exception e) {
            log.error("Failed to load file " + tempFile, e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to load file " + tempFile + " with error " + e.getMessage())));
        }
    }
}
