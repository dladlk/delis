package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentErrorRepository;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.util.ClassLoaderUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class JournalDocumentServiceImpl implements JournalDocumentService {

    private final DocumentRepository documentRepository;
    private final JournalDocumentRepository journalDocumentRepository;
    private final JournalDocumentErrorRepository journalDocumentErrorRepository;
    private final AbstractGenerateDataService<JournalDocumentRepository, JournalDocument> abstractGenerateDataService;

    @Autowired
    public JournalDocumentServiceImpl(
            DocumentRepository documentRepository,
            JournalDocumentRepository journalDocumentRepository,
            JournalDocumentErrorRepository journalDocumentErrorRepository,
            AbstractGenerateDataService<JournalDocumentRepository, JournalDocument> abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.journalDocumentRepository = journalDocumentRepository;
        this.journalDocumentErrorRepository = journalDocumentErrorRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<JournalDocument> getAll(WebRequest webRequest) {

        return abstractGenerateDataService.generateDataPageContainer(JournalDocument.class, webRequest, journalDocumentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public JournalDocument getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, JournalDocument.class, journalDocumentRepository);
    }

	@Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
	public ListContainer<JournalDocument> getByDocument(WebRequest webRequest, long documentId) {
        long collectionSize = journalDocumentRepository.countByDocumentId(documentId);
        if (collectionSize == 0) {
            return new ListContainer<>(Collections.emptyList());
        }
        String[] strings = Objects.requireNonNull(webRequest.getParameter("sort")).split("_");
        for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(JournalDocument.class) ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Objects.equals(strings[1].toUpperCase(), field.getName().toUpperCase())) {
                    if (Objects.equals(strings[2], "Asc")) {
                        return new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by(field.getName()).ascending()));
                    } else {
                        return new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by(field.getName()).descending()));
                    }
                }
            }
        }
        return  new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by("id").ascending()));
	}

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<JournalDocumentError> getByJournalDocumentDocumentId(long documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (Objects.isNull(document)) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document not found by id")));
        }
        long collectionSize = journalDocumentErrorRepository.countByJournalDocumentDocument(document);
        if (collectionSize == 0) {
            return new ListContainer<>(Collections.emptyList());
        } else {
            return new ListContainer<>(
                    journalDocumentErrorRepository.findAllByJournalDocumentDocumentOrderById(document));
        }
    }
}
