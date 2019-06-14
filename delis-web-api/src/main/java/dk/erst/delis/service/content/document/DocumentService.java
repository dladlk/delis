package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.rest.data.request.document.UpdateDocumentStatusData;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.service.content.AbstractService;

import java.nio.file.Path;

public interface DocumentService extends AbstractService<Document> {

    ListContainer<DocumentBytes> findListDocumentBytesByDocumentId(Long documentId);
    void updateDocumentStatus(UpdateDocumentStatusData data);
    Path downloadFile(Long id, Long bytesId);
}
