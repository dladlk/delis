package dk.erst.delis.service.journal.organisation;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationRepository;
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
public class JournalOrganisationServiceImpl implements JournalOrganisationService {

    private final JournalOrganisationRepository journalOrganisationRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalOrganisationServiceImpl(JournalOrganisationRepository journalOrganisationRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalOrganisationRepository = journalOrganisationRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalOrganisation> getAllAfterFilteringAndSorting(int page, int size, WebRequest request) {

        long collectionSize = journalOrganisationRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, journalOrganisationRepository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(JournalOrganisation.class, sort, request, page, size, collectionSize, journalOrganisationRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(JournalOrganisation.class, request, page, size, collectionSize, journalOrganisationRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JournalOrganisation getOneById(long id) {
        JournalOrganisation journalOrganisation = journalOrganisationRepository.findById(id).orElse(null);
        if (journalOrganisation == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "journalOrganisation not found by id")));
        }
        return journalOrganisation;
    }
}
