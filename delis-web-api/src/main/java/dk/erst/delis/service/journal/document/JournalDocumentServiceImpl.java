package dk.erst.delis.service.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.journal.document.JournalDocumentFilterModel;
import dk.erst.delis.persistence.journal.document.JournalDocumentRepository;
import dk.erst.delis.persistence.journal.document.JournalDocumentSpecification;
import dk.erst.delis.rest.data.request.param.DateRequestModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.journal.document.JournalDocumentConstants.*;

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
            return new PageContainer<>();
        }

        List<String> filters = WebRequestUtil.existParameters(request);

        if (CollectionUtils.isNotEmpty(filters)) {

            JournalDocumentFilterModel filterModel = new JournalDocumentFilterModel();

            DateRequestModel dateRequestModel = WebRequestUtil.generateDateRequestModel(request);
            if (Objects.nonNull(dateRequestModel)) {
                filterModel.setStart(dateRequestModel.getStart());
                filterModel.setEnd(dateRequestModel.getEnd());
            } else {
                filterModel.setStart(journalDocumentRepository.findMinCreateTime());
                filterModel.setEnd(journalDocumentRepository.findMaxCreateTime());
            }

            for (String key : filters) {
                if (key.equals(ORGANIZATION_FIELD)) {
                    filterModel.setOrganisation(request.getParameter(ORGANIZATION_FIELD));
                }
                if (key.equals(DOCUMENT_FIELD)) {
                    filterModel.setDocument(request.getParameter(DOCUMENT_FIELD));
                }
                if (key.equals(DOCUMENT_PROCESS_STEP_TYPE_FIELD)) {
                    System.out.println(request.getParameter(DOCUMENT_PROCESS_STEP_TYPE_FIELD));
                    filterModel.setType(DocumentProcessStepType.valueOf(request.getParameter(DOCUMENT_PROCESS_STEP_TYPE_FIELD)));
                }
                if (key.equals(SUCCESS_FIELD)) {
                    filterModel.setSuccess(request.getParameter(SUCCESS_FIELD));
                }
                if (key.equals(MESSAGE_FIELD)) {
                    filterModel.setMessage(request.getParameter(MESSAGE_FIELD));
                }
                if (key.equals(DURATION_MS_FIELD)) {
                    filterModel.setDurationMs(Long.parseLong(Objects.requireNonNull(request.getParameter(DURATION_MS_FIELD))));
                }
            }

            String sort = WebRequestUtil.existSortParameter(filters);

            if (StringUtils.isNotBlank(sort)) {
                return abstractGenerateDataService.sortProcess(JournalDocument.class, sort, request, page, size, collectionSize, journalDocumentRepository, filterModel, new JournalDocumentSpecification());
            } else {
                return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, journalDocumentRepository, filterModel, new JournalDocumentSpecification());
            }
        }

        return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, journalDocumentRepository);
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
