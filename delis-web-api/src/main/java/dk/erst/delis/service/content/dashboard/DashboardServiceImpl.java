package dk.erst.delis.service.content.dashboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentStatus;
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
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.util.DateUtil;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final DocumentRepository documentRepository;
    private final SendDocumentRepository sendDocumentRepository;
    private final SecurityService securityService;

    @Autowired
    public DashboardServiceImpl(
            SecurityService securityService,
            DocumentRepository documentRepository,
            SendDocumentRepository sendDocumentRepository) {
        this.documentRepository = documentRepository;
        this.sendDocumentRepository = sendDocumentRepository;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public DashboardData generateDashboardData() {

        Long organisationId = loadOrganisationId();
        log.info("Start dashboard loading, orgId = " + organisationId + ", now: " + new Date());

        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        List<DocumentStatus> documentStatusErrors = Arrays.stream(DocumentStatus.values()).filter(DocumentStatus::isError).collect(Collectors.toList());
        DashboardData data = new DashboardData();

        if (organisationId == null) {
            data.setReceivedDocumentsLastHour( documentRepository.countAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
            data.setSendDocumentsLastHour(sendDocumentRepository.countAllByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
            data.setErrorLastHour(documentRepository.countByCreateTimeBetweenAndDocumentStatusIn(dateRange.getStart(), dateRange.getEnd(), documentStatusErrors));
        } else {
            data.setReceivedDocumentsLastHour(documentRepository.countByCreateTimeBetweenAndOrganisationId(dateRange.getStart(), dateRange.getEnd(), organisationId));
            data.setSendDocumentsLastHour(sendDocumentRepository.countByCreateTimeBetweenAndOrganisationId(dateRange.getStart(), dateRange.getEnd(), organisationId));
            data.setErrorLastHour(documentRepository.countByCreateTimeBetweenAndOrganisationIdAndDocumentStatusIn(dateRange.getStart(), dateRange.getEnd(), organisationId, documentStatusErrors));
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
