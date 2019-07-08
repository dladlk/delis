package dk.erst.delis.service.content.chart;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.document.SendDocumentRepository;
import dk.erst.delis.persistence.repository.organization.OrganizationRepository;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.DateUtil;
import dk.erst.delis.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

//import static dk.erst.delis.util.DateUtil.DEFAULT_TIME_ZONE;

@Service
public class ChartServiceImpl implements ChartService {

    private static final String CHART_INDEX_DOCUMENT = "0";
    private static final String CHART_INDEX_ERROR_DOCUMENT = "1";
    private static final String CHART_INDEX_SEND_DOCUMENT = "2";

    private final DocumentRepository documentRepository;
    private final SendDocumentRepository sendDocumentRepository;
    private final OrganizationRepository organizationRepository;
    private final SecurityService securityService;

    @Autowired
    public ChartServiceImpl(
            DocumentRepository documentRepository,
            OrganizationRepository organizationRepository,
            SendDocumentRepository sendDocumentRepository,
            SecurityService securityService) {
        this.documentRepository = documentRepository;
        this.sendDocumentRepository = sendDocumentRepository;
        this.organizationRepository = organizationRepository;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ChartData generateChartData(WebRequest request) {

        String chartIndex = request.getParameter("chartIndex");

        boolean defaultChart = false;
        String defaultChartParameter = request.getParameter("defaultChart");
        if (StringUtils.isNotBlank(defaultChartParameter)) {
            defaultChart = Boolean.parseBoolean(defaultChartParameter);
        }

        Date start = null, end = null;
        String endDateParameter = request.getParameter("endDate");
        if (Objects.nonNull(endDateParameter)) {
            end = new Date(Long.parseLong(endDateParameter));
        }
        String startDateParameter = request.getParameter("startDate");
        if (Objects.nonNull(startDateParameter)) {
            start = new Date(Long.parseLong(startDateParameter));
        }

        if (defaultChart) {
            return generateDefaultChartData(chartIndex);
        } else {
            return generateCustomChartData(start, end, chartIndex);
        }
    }

    private ChartData generateDefaultChartData(String chartIndex) {
        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        Date start = DateUtil.generateBeginningOfDay(new Date());
        Date end = new Date();
        long hours = DateUtil.getHoursBetween(start, end) + 1;
        lineChartDataContent.setLabel("chart data default");
        List<Long> dataGraph = new ArrayList<>();
        for (int h = 0; h <= hours; ++h) {
            lineChartLabels.add(DateUtil.DATE_FORMAT_BY_DAY.format(start));
            end = DateUtil.addHour(start, 1);
            dataGraph.add(generateDataGraphCount(start, end, chartIndex));
            start = new Date(end.getTime());
        }
        lineChartDataContent.setData(dataGraph);
        lineChartData.add(lineChartDataContent);
        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);
        return chartData;
    }

    private ChartData generateCustomChartData(Date start, Date end, String chartIndex) {

        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        long days = DateUtil.getMinutesBetween(start, end) / 60;
        if (days > 24) {
            days /= 24;
            lineChartDataContent.setLabel("chart data custom");
            List<Long> dataGraph = new ArrayList<>();
            end = DateUtil.addDay(start, 1);
            for (int d = 0; d <= days; ++d) {
                lineChartLabels.add(DateUtil.DATE_FORMAT_BY_CUSTOM_PERIOD.format(start));
                dataGraph.add(generateDataGraphCount(start, end, chartIndex));
                start = DateUtil.addDay(start, 1);
                end = DateUtil.addDay(start, 1);
            }
            lineChartDataContent.setData(dataGraph);
            lineChartData.add(lineChartDataContent);
            chartData.setLineChartData(lineChartData);
            chartData.setLineChartLabels(lineChartLabels);
            return chartData;
        } else {
            return generateDefaultChartData(chartIndex);
        }
    }

    private Long generateDataGraphCount(Date startSearchDate, Date endSearchDate, String chartIndex) {
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (orgId == null) {
                conflictProcess();
            }
            Organisation organisation = organizationRepository.findById(orgId).orElse(null);
            if (organisation == null) {
                conflictProcess();
            }
            if (chartIndex != null) {
                if (StringUtils.equals(chartIndex, CHART_INDEX_DOCUMENT)) {
                    return documentRepository.countByCreateTimeBetweenAndOrganisationAndLastErrorIsNull(startSearchDate, endSearchDate, organisation);
                } else if (StringUtils.equals(chartIndex, CHART_INDEX_ERROR_DOCUMENT)) {
                    return documentRepository.countByCreateTimeBetweenAndOrganisationAndLastErrorIsNotNull(startSearchDate, endSearchDate, organisation);
                } else {
                    return sendDocumentRepository.countByCreateTimeBetweenAndOrganisation(startSearchDate, endSearchDate, organisation);
                }
            } else {
                return documentRepository.countByCreateTimeBetweenAndOrganisation(startSearchDate, endSearchDate, organisation);
            }
        } else {
            if (chartIndex != null) {
                if (StringUtils.equals(chartIndex, CHART_INDEX_DOCUMENT)) {
                    return documentRepository.countByCreateTimeBetweenAndLastErrorIsNull(startSearchDate, endSearchDate);
                } else if (StringUtils.equals(chartIndex, CHART_INDEX_ERROR_DOCUMENT)) {
                    return documentRepository.countByCreateTimeBetweenAndLastErrorIsNotNull(startSearchDate, endSearchDate);
                } else {
                    return sendDocumentRepository.countByCreateTimeBetween(startSearchDate, endSearchDate);
                }
            } else {
                return documentRepository.countByCreateTimeBetween(startSearchDate, endSearchDate);
            }
        }
    }

    private void conflictProcess() {
        throw new RestConflictException(Collections.singletonList(
                new FieldErrorModel("organization", HttpStatus.CONFLICT.getReasonPhrase(), "there was a problem reading your organization")));
    }


//    @Override
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @Transactional(readOnly = true)
//    public ChartData generateChartData(WebRequest request) {
//
//        String timeZone = request.getParameter("timeZone");
//        if (StringUtils.isBlank(timeZone)) {
//            timeZone = DEFAULT_TIME_ZONE;
//        }
//
//        boolean defaultChart = false;
//        String defaultChartParameter = request.getParameter("defaultChart");
//        if (StringUtils.isNotBlank(defaultChartParameter)) {
//            defaultChart = Boolean.parseBoolean(defaultChartParameter);
//        }
//
//        Date start = null, end = null;
//        String endDateParameter = request.getParameter("endDate");
//        if (Objects.nonNull(endDateParameter)) {
//            end = new Date(Long.parseLong(endDateParameter));
//        }
//        String startDateParameter = request.getParameter("startDate");
//        if (Objects.nonNull(startDateParameter)) {
//            start = new Date(Long.parseLong(startDateParameter));
//        }
//
//        if (defaultChart) {
//            return generateDefaultChartData(start, DateUtil.convertClientTimeToServerTime(timeZone, null, true), end);
//        } else {
//            return generateCustomChartData(start, end, timeZone);
//        }
//    }

//    private ChartData generateDefaultChartData(Date clientDate, Date startSearchDate, Date endSearchDate) {
//        ChartData chartData = new ChartData();
//        List<LineChartData> lineChartData = new ArrayList<>();
//        List<String> lineChartLabels = new ArrayList<>();
//        LineChartData lineChartDataContent = new LineChartData();
//        Date end = DateUtil.addHour(startSearchDate, 1);
//        long hours = DateUtil.getHoursBetween(endSearchDate, startSearchDate);
//        lineChartDataContent.setLabel("chart data default");
//        List<Long> dataGraph = new ArrayList<>();
//        for (int h = 0 ; h <= hours ; ++h) {
//            lineChartLabels.add(DateUtil.DATE_FORMAT_BY_DAY.format(clientDate));
//            dataGraph.add(generateDataGraphCount(startSearchDate, endSearchDate));
//            startSearchDate = new Date(end.getTime());
//            clientDate = DateUtil.addHour(clientDate, 1);
//            end = DateUtil.addHour(startSearchDate, 1);
//        }
//        lineChartDataContent.setData(dataGraph);
//        lineChartData.add(lineChartDataContent);
//        chartData.setLineChartData(lineChartData);
//        chartData.setLineChartLabels(lineChartLabels);
//        return chartData;
//    }
}
