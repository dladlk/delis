package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.persistence.repository.document.DocumentBytesRepository;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentBytesRepository documentBytesRepository;
    private final AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService;

    @Autowired
    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            DocumentBytesRepository documentBytesRepository,
            AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.documentBytesRepository = documentBytesRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public PageContainer<Document> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Document.class, webRequest, documentRepository);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, Document.class, documentRepository);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public ListContainer<DocumentBytes> findListDocumentBytesByDocumentId(Long documentId) {
        Document document = abstractGenerateDataService.getOneById(documentId, Document.class, documentRepository);
        long totalElements = documentBytesRepository.countByDocument(document);
        if (totalElements == 0) {
            return new ListContainer<>(Collections.emptyList());
        } else {
            return new ListContainer<>(documentBytesRepository.findByDocument(document));
        }
    }
}
