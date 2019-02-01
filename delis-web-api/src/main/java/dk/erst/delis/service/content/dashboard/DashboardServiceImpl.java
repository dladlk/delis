package dk.erst.delis.service.content.dashboard;

import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.identifier.IdentifierRepository;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentRepository;
import dk.erst.delis.persistence.repository.journal.identifier.JournalIdentifierRepository;
import dk.erst.delis.persistence.repository.journal.organisation.JournalOrganisationRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * @author funtusthan, created by 22.01.19
 */

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DocumentRepository documentRepository;
    private final IdentifierRepository identifierRepository;
    private final JournalDocumentRepository journalDocumentRepository;
    private final JournalIdentifierRepository journalIdentifierRepository;
    private final JournalOrganisationRepository journalOrganisationRepository;

    @Autowired
    public DashboardServiceImpl(
            DocumentRepository documentRepository,
            JournalDocumentRepository journalDocumentRepository,
            JournalIdentifierRepository journalIdentifierRepository,
            JournalOrganisationRepository journalOrganisationRepository,
            IdentifierRepository identifierRepository) {
        this.documentRepository = documentRepository;
        this.journalDocumentRepository = journalDocumentRepository;
        this.journalIdentifierRepository = journalIdentifierRepository;
        this.journalOrganisationRepository = journalOrganisationRepository;
        this.identifierRepository = identifierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardData generateDashboardData() {

        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        DashboardData data = new DashboardData();

        data.setJournalDocument(journalDocumentRepository.count());
        data.setJournalIdentifier(journalIdentifierRepository.count());
        data.setJournalOrganisation(journalOrganisationRepository.count());
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
