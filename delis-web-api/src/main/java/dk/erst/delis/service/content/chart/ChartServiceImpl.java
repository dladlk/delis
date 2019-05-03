package dk.erst.delis.service.content.chart;

import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.util.DateUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

import static dk.erst.delis.util.DateUtil.DEFAULT_TIME_ZONE;

/**
 * @author funtusthan, created by 23.01.19
 */

@Service
public class ChartServiceImpl implements ChartService {

    private final DocumentRepository documentRepository;

    @Autowired
    public ChartServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public ChartData generateChartData(WebRequest request) {

        String timeZone = request.getParameter("timeZone");
        if (StringUtils.isBlank(timeZone)) {
            timeZone = DEFAULT_TIME_ZONE;
        }

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
            return generateDefaultChartData(start, DateUtil.convertClientTimeToServerTime(timeZone, null, true), end);
        } else {
            return generateCustomChartData(start, end, timeZone);
        }
    }

    private ChartData generateCustomChartData(Date start, Date end, String timeZone) {

        Date startSearchDate = DateUtil.convertClientTimeToServerTime(timeZone, start, true);
        Date endSearchDate = DateUtil.convertClientTimeToServerTime(timeZone, end, false);

        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        long days = DateUtil.getMinutesBetween(startSearchDate, endSearchDate) / 60;
        if (days > 24) {
            days /= 24;
            lineChartDataContent.setLabel("chart data custom");
            List<Long> dataGraph = new ArrayList<>();
            endSearchDate = DateUtil.addDay(startSearchDate, 1);
            for (int d = 0 ; d <= days ; ++d) {
                lineChartLabels.add(DateUtil.DATE_FORMAT_BY_CUSTOM_PERIOD.format(start));
                dataGraph.add(documentRepository.countByCreateTimeBetween(startSearchDate, endSearchDate));
                start = DateUtil.addDay(start, 1);
                startSearchDate = new Date(endSearchDate.getTime());
                endSearchDate = DateUtil.addDay(startSearchDate, 1);
            }
            lineChartDataContent.setData(dataGraph);
            lineChartData.add(lineChartDataContent);
            chartData.setLineChartData(lineChartData);
            chartData.setLineChartLabels(lineChartLabels);
            return chartData;
        } else {
            return generateDefaultChartData(start, startSearchDate, endSearchDate);
        }
    }

    private ChartData generateDefaultChartData(Date clientDate, Date startSearchDate, Date endSearchDate) {
        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        Date end = DateUtil.addHour(startSearchDate, 1);
        long hours = DateUtil.getHoursBetween(endSearchDate, startSearchDate);
        lineChartDataContent.setLabel("chart data default");
        List<Long> dataGraph = new ArrayList<>();
        for (int h = 0 ; h <= hours ; ++h) {
            lineChartLabels.add(DateUtil.DATE_FORMAT_BY_DAY.format(clientDate));
            dataGraph.add(documentRepository.countByCreateTimeBetween(startSearchDate, end));
            startSearchDate = new Date(end.getTime());
            clientDate = DateUtil.addHour(clientDate, 1);
            end = DateUtil.addHour(startSearchDate, 1);
        }
        lineChartDataContent.setData(dataGraph);
        lineChartData.add(lineChartDataContent);
        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);
        return chartData;
    }
}
