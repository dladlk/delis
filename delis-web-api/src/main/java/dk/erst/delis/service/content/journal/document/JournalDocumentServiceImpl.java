package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
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
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author funtusthan, created by 13.01.19
 */

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
    @Transactional(readOnly = true)
    public PageContainer<JournalDocument> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalDocument.class, webRequest, journalDocumentRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalDocument getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, JournalDocument.class, journalDocumentRepository);
    }

	@Override
    @Transactional(readOnly = true)
	public ListContainer<JournalDocument> getByDocument(WebRequest webRequest, long documentId) {
        long collectionSize = journalDocumentRepository.countByDocumentId(documentId);
        if (collectionSize == 0) {
            return new ListContainer<>(Collections.emptyList());
        }
        String sort = WebRequestUtil.existSortParameter(webRequest);
        if (StringUtils.isNotBlank(sort)) {
            List<Field> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(JournalDocument.class.getDeclaredFields()));
            fields.addAll(Arrays.asList(JournalDocument.class.getSuperclass().getDeclaredFields()));
            for ( Field field : fields ) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    if (sort.toUpperCase().endsWith(field.getName().toUpperCase())) {
                        int count = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(sort)));
                        if (count == 1) {
                            return new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by(field.getName()).descending()));
                        } else if (count == 2) {
                            return new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by(field.getName()).ascending()));
                        }
                    }
                }
            }
        }
        return  new ListContainer<>(journalDocumentRepository.findAllByDocumentId(documentId, Sort.by("id").ascending()));
	}

    @Override
    @Transactional(readOnly = true)
    public ListContainer<ErrorDictionary> getByJournalDocumentDocumentId(long documentId) {
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
                    journalDocumentErrorRepository.findAllByJournalDocumentDocumentOrderById(document)
                            .stream()
                            .map(JournalDocumentError::getErrorDictionary)
                            .collect(Collectors.toList()));
        }
    }
}
