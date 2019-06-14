package dk.erst.delis.rest.controller.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.service.content.document.DocumentDelisWebApiService;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.ir.InvoiceResponseFormControllerConst;
import dk.erst.delis.web.document.ir.MessageLevelResponseConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.Collections;

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentInvoiceRestController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentDelisWebApiService documentDelisWebApiService;

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
}
