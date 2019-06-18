package dk.erst.delis.service.content.journal.organisation;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.journal.organisation.JournalOrganisationRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Objects;

@Service
public class JournalOrganisationDelisWebApiServiceImpl implements JournalOrganisationDelisWebApiService {

    private final SecurityService securityService;
    private final JournalOrganisationRepository journalOrganisationRepository;
    private final AbstractGenerateDataService<JournalOrganisationRepository, JournalOrganisation> abstractGenerateDataService;

    @Autowired
    public JournalOrganisationDelisWebApiServiceImpl(JournalOrganisationRepository journalOrganisationRepository, AbstractGenerateDataService<JournalOrganisationRepository, JournalOrganisation> abstractGenerateDataService, SecurityService securityService) {
        this.journalOrganisationRepository = journalOrganisationRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<JournalOrganisation> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalOrganisation.class, webRequest, journalOrganisationRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public JournalOrganisation getOneById(long id) {
        JournalOrganisation journalOrganisation = journalOrganisationRepository.findById(id).orElse(null);
        if (journalOrganisation == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "JournalOrganisation not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, journalOrganisation.getOrganisation().getId())) {
                return journalOrganisation;
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return journalOrganisation;
        }
    }
}
