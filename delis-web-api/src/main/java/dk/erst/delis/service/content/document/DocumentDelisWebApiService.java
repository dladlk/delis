package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.service.content.AbstractService;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;

public interface DocumentDelisWebApiService extends AbstractService<Document> {

    ListContainer<DocumentBytes> findListDocumentBytesByDocumentId(Long documentId);
    byte[] downloadFile(Long id, Long bytesId);
    InvoiceResponseData getInvoiceResponseDataByDocument(long id);
    Object generateInvoiceResponse(InvoiceResponseForm invoiceResponseForm);
}
