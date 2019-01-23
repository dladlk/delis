package dk.erst.delis.service.content.dashboard;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.persistence.identifier.IdentifierRepository;
import dk.erst.delis.persistence.journal.document.JournalDocumentRepository;
import dk.erst.delis.persistence.journal.identifier.JournalIdentifierRepository;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.util.DateUtil;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        DashboardData data = new DashboardData();

        data.setJournalDocument(journalDocumentRepository.count());
        data.setJournalIdentifier(journalIdentifierRepository.count());
        data.setJournalOrganisation(journalOrganisationRepository.count());

        DateRangeModel dateRange = DateUtil.generateDateRangeByLastHour();
        List<Identifier> identifiers = identifierRepository.findByPublishingStatusAndCreateTimeBetween(IdentifierPublishingStatus.DONE, dateRange.getStart(), dateRange.getEnd());
        if (CollectionUtils.isNotEmpty(identifiers)) {
            data.setPublishedLastHour(documentRepository.countByReceiverIdentifierIn(identifiers));
        } else {
            data.setPublishedLastHour(0);
        }

        long errors = documentRepository.countByLastErrorNotNull();
        if (errors == 0) {
            errors = documentRepository.countByDocumentStatusIn(Arrays.asList(DocumentStatus.LOAD_ERROR, DocumentStatus.VALIDATE_ERROR));
        }
        data.setErrorLastHour(errors);

        data.setReceivedDocumentsLastHour(documentRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));

        data.setAverageDocumentsLastHour(0); // todo what is Average?

        // generate chart data by last hour by interval of 10 minutes

        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();

        LineChartData lineChartDataContent = new LineChartData();
        lineChartDataContent.setLabel("chart data by last hour by interval of 10 minutes");
        List<String> dataGraf = new ArrayList<>();
        int[] minutes = {60, 50, 40, 30, 20, 10};
        for (int minute : minutes) {
            dateRange = DateUtil.generateDateRangeByFromAndToLastHour(minute, 10);
            System.out.println("!!!!!!!!!!");
            System.out.println("start = " + dateRange.getStart());
            System.out.println("end = " + dateRange.getEnd());
            lineChartLabels.add(String.valueOf(dateRange.getStart()));
            dataGraf.add(String.valueOf(documentRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd())));
        }
        lineChartDataContent.setData(dataGraf);
        lineChartData.add(lineChartDataContent);

        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);
        data.setChartData(chartData);

        return data;
    }
}
