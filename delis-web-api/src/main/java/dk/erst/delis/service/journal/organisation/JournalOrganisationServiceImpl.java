package dk.erst.delis.service.journal.organisation;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationFilterModel;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationRepository;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationSpecification;
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

import static dk.erst.delis.persistence.journal.organisation.JournalOrganisationConstants.*;

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
            return new PageContainer<>();
        }

        List<String> filters = WebRequestUtil.existParameters(request);

        if (CollectionUtils.isNotEmpty(filters)) {

            JournalOrganisationFilterModel filterModel = new JournalOrganisationFilterModel();

            DateRequestModel dateRequestModel = WebRequestUtil.generateDateRequestModel(request);
            if (Objects.nonNull(dateRequestModel)) {
                filterModel.setStart(dateRequestModel.getStart());
                filterModel.setEnd(dateRequestModel.getEnd());
            } else {
                filterModel.setStart(journalOrganisationRepository.findMinCreateTime());
                filterModel.setEnd(journalOrganisationRepository.findMaxCreateTime());
            }

            for (String key : filters) {
                if (key.equals(ORGANIZATION_FIELD)) {
                    filterModel.setOrganisation(request.getParameter(ORGANIZATION_FIELD));
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
                return abstractGenerateDataService.sortProcess(JournalOrganisation.class, sort, request, page, size, collectionSize, journalOrganisationRepository, filterModel, new JournalOrganisationSpecification());
            } else {
                return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, journalOrganisationRepository, filterModel, new JournalOrganisationSpecification());
            }
        }

        return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, journalOrganisationRepository);
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
