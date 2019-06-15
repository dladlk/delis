package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.DocumentBytesRepository;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@Slf4j
@Service
public class DocumentDelisWebApiServiceImpl implements DocumentDelisWebApiService {

    private final DocumentRepository documentRepository;
    private final DocumentBytesRepository documentBytesRepository;
    private final AbstractGenerateDataService<DocumentRepository, Document> abstractGenerateDataService;
    @Value("${delis.download.allow.all:#{false}}")
    private boolean downloadAllowAll;

    @Autowired
    public DocumentDelisWebApiServiceImpl(
            DocumentRepository documentRepository,
            DocumentBytesRepository documentBytesRepository,
            AbstractGenerateDataService<DocumentRepository, Document> abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.documentBytesRepository = documentBytesRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<Document> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Document.class, webRequest, documentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, Document.class, documentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
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

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public DocumentBytes findByIdAndDocumentId(Long id, Long bytesId) {
        DocumentBytes documentBytes = documentBytesRepository.findByIdAndDocumentId(bytesId, id);
        if (documentBytes == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.NOT_FOUND.getReasonPhrase(), "Data not found")));
        }
        if (!isDownloadAllowed(documentBytes)) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("RECEIPT", HttpStatus.CONFLICT.getReasonPhrase(), "Only RECEIPT bytes are allowed for download, but " + documentBytes.getType() + " is requested")));
        }
        return documentBytes;
    }

    private boolean isDownloadAllowed(DocumentBytes b) {
        if (downloadAllowAll) {
            return true;
        }
        return b.getType() == DocumentBytesType.IN_AS4;
    }
}
