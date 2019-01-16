package dk.erst.delis.service.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.journal.document.JournalDocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

/**
 * @author funtusthan, created by 13.01.19
 */

@Service
public class JournalDocumentServiceImpl implements JournalDocumentService {

    private final JournalDocumentRepository journalDocumentRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalDocumentServiceImpl(JournalDocumentRepository journalDocumentRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalDocumentRepository = journalDocumentRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalDocument> getAllAfterFilteringAndSorting(int page, int size, WebRequest request) {

        long collectionSize = journalDocumentRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, journalDocumentRepository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(JournalDocument.class, sort, request, page, size, collectionSize, journalDocumentRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(JournalDocument.class, request, page, size, collectionSize, journalDocumentRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JournalDocument getOneById(long id) {
        JournalDocument journalDocument = journalDocumentRepository.findById(id).orElse(null);
        if (journalDocument == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "journalDocument not found by id")));
        }
        return journalDocument;
    }
}
