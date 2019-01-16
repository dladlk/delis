package dk.erst.delis.service.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.journal.identifier.JournalIdentifierRepository;
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
 * @author funtusthan, created by 14.01.19
 */

@Service
public class JournalIdentifierServiceImpl implements JournalIdentifierService {

    private final JournalIdentifierRepository journalIdentifierRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalIdentifierServiceImpl(JournalIdentifierRepository journalIdentifierRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalIdentifierRepository = journalIdentifierRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalIdentifier> getAllAfterFilteringAndSorting(int page, int size, WebRequest request) {

        long collectionSize = journalIdentifierRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, journalIdentifierRepository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(JournalIdentifier.class, sort, request, page, size, collectionSize, journalIdentifierRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(JournalIdentifier.class, request, page, size, collectionSize, journalIdentifierRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JournalIdentifier getOneById(long id) {
        JournalIdentifier journalIdentifier = journalIdentifierRepository.findById(id).orElse(null);
        if (journalIdentifier == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "journalOrganisation not found by id")));
        }
        return journalIdentifier;
    }
}
