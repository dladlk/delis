package dk.erst.delis.service.content.dashboard;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.identifier.IdentifierRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.util.DateUtil;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DocumentRepository documentRepository;
    private final IdentifierRepository identifierRepository;

    @Autowired
    public DashboardServiceImpl(
            DocumentRepository documentRepository,
            IdentifierRepository identifierRepository) {
        this.documentRepository = documentRepository;
        this.identifierRepository = identifierRepository;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public DashboardData generateDashboardData() {

        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        DashboardData data = new DashboardData();

        data.setIdentifierLastHour(identifierRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));

        long errors = documentRepository.countByLastErrorNotNullAndCreateTimeBetween(dateRange.getStart(), dateRange.getEnd());
        if (errors == 0) {
            errors = documentRepository.countByDocumentStatusInAndCreateTimeBetween(
                    Arrays.asList(
                            DocumentStatus.LOAD_ERROR,
                            DocumentStatus.VALIDATE_ERROR,
                            DocumentStatus.UNKNOWN_RECEIVER),
                    dateRange.getStart(), dateRange.getEnd());
        }
        data.setErrorLastHour(errors);

        data.setReceivedDocumentsLastHour(documentRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));

        data.setAverageDocumentsLastHour(0); // todo what is Average?

        return data;
    }
}
