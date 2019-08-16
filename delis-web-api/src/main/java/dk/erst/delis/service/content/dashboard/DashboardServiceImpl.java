package dk.erst.delis.service.content.dashboard;

import java.util.Collections;
import java.util.Date;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.persistence.repository.document.SendDocumentRepository;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.identifier.IdentifierRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.util.DateUtil;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final DocumentRepository documentRepository;
    private final SendDocumentRepository sendDocumentRepository;
    private final IdentifierRepository identifierRepository;
    private final SecurityService securityService;

    @Autowired
    public DashboardServiceImpl(
            SecurityService securityService,
            DocumentRepository documentRepository,
            SendDocumentRepository sendDocumentRepository,
            IdentifierRepository identifierRepository) {
        this.documentRepository = documentRepository;
        this.sendDocumentRepository = sendDocumentRepository;
        this.identifierRepository = identifierRepository;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public DashboardData generateDashboardData() {

        Long organisationId = loadOrganisationId();
        log.info("Start dashboard loading, orgId = " + organisationId + ", now: " + new Date());

        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        DashboardData data = new DashboardData();

        if (organisationId == null) {
            data.setIdentifierLastHour(identifierRepository.countAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
            data.setReceivedDocumentsLastHour( documentRepository.countAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
            data.setSendDocumentsLastHour(sendDocumentRepository.countAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
        } else {
            data.setIdentifierLastHour(identifierRepository.countByCreateTimeBetweenAndOrganisationId(dateRange.getStart(), dateRange.getEnd(), organisationId));
            data.setReceivedDocumentsLastHour(documentRepository.countByCreateTimeBetweenAndOrganisationId(dateRange.getStart(), dateRange.getEnd(), organisationId));
            data.setSendDocumentsLastHour(sendDocumentRepository.countByCreateTimeBetweenAndOrganisationId(dateRange.getStart(), dateRange.getEnd(), organisationId));
        }

        return data;
    }

    private Long loadOrganisationId() {
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Organisation organisation = securityService.getOrganisation();
            Long orgId = organisation.getId();
            if (orgId == null) {
                conflictProcess();
            }
            return organisation.getId();
        }
        return null;
    }

    private void conflictProcess() {
        throw new RestConflictException(Collections.singletonList(new FieldErrorModel("organization", HttpStatus.CONFLICT.getReasonPhrase(), "there was a problem reading your organization")));
    }
}
