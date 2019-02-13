package dk.erst.delis.service.content.chart;

import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

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
    @Transactional(readOnly = true)
    public ChartData generateChartData(WebRequest request) {
        Date start = null, end = null;
        String endDateParameter = request.getParameter("endDate");
        if (Objects.nonNull(endDateParameter)) {
            end = DateUtil.generateEndOfDay(new Date(Long.parseLong(endDateParameter)));
        }
        String startDateParameter = request.getParameter("startDate");
        if (Objects.nonNull(startDateParameter)) {
            start = DateUtil.generateBeginningOfDay(new Date(Long.parseLong(startDateParameter)));
        }
        if (Objects.isNull(start) && Objects.isNull(end)) {
            return generateDefaultChartData(new Date());
        } else {
            return generateCustomChartData(start, end);
        }
    }

    private ChartData generateCustomChartData(Date start, Date end) {
        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        long days = DateUtil.rangeHoursDate(start, end) / 60;
        if (days > 24) {
            days /= 24;
            lineChartDataContent.setLabel("chart data custom");
            List<Long> dataGraph = new ArrayList<>();
            end = DateUtil.addDay(start, 1);
            for (int d = 0 ; d <= days ; ++d) {
                lineChartLabels.add(DateUtil.DATE_FORMAT_BY_CUSTOM_PERIOD.format(start));
                dataGraph.add(documentRepository.countByCreateTimeBetween(start, end));
                start = new Date(end.getTime());
                end = DateUtil.addDay(start, 1);
            }
            lineChartDataContent.setData(dataGraph);
            lineChartData.add(lineChartDataContent);
            chartData.setLineChartData(lineChartData);
            chartData.setLineChartLabels(lineChartLabels);
            return chartData;
        } else {
            return generateDefaultChartData(start);
        }
    }

    private ChartData generateDefaultChartData(Date date) {
        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();
        LineChartData lineChartDataContent = new LineChartData();
        Date start = DateUtil.generateBeginningOfDay(date);
        Date end = DateUtil.addHour(start, 1);
        long hours = DateUtil.rangeHoursDate(start, new Date()) / 60;
        lineChartDataContent.setLabel("chart data default");
        List<Long> dataGraph = new ArrayList<>();
        for (int h = 0 ; h <= hours ; ++h) {
            lineChartLabels.add(DateUtil.DATE_FORMAT_BY_DAY.format(start));
            dataGraph.add(documentRepository.countByCreateTimeBetween(start, end));
            start = new Date(end.getTime());
            end = DateUtil.addHour(start, 1);
        }
        lineChartDataContent.setData(dataGraph);
        lineChartData.add(lineChartDataContent);
        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);
        return chartData;
    }
}
