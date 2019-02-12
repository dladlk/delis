package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentRepository;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author funtusthan, created by 13.01.19
 */

@Service
public class JournalDocumentServiceImpl implements JournalDocumentService {

    private final JournalDocumentRepository journalDocumentRepository;
    private final AbstractGenerateDataService<JournalDocumentRepository, JournalDocument> abstractGenerateDataService;

    @Autowired
    public JournalDocumentServiceImpl(JournalDocumentRepository journalDocumentRepository, AbstractGenerateDataService<JournalDocumentRepository, JournalDocument> abstractGenerateDataService) {
        this.journalDocumentRepository = journalDocumentRepository;
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
}
