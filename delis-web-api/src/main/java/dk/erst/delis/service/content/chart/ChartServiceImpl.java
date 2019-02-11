package dk.erst.delis.service.content.chart;

import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author funtusthan, created by 23.01.19
 */

@Service
public class ChartServiceImpl implements ChartService {

    private static final int DEFAULT_INTERVAL_OF_MINUTES = 10;
    private static final int DEFAULT_INTERVAL_OF_HOUR = 1;

    private final DocumentRepository documentRepository;

    @Autowired
    public ChartServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ChartData generateChartData(WebRequest request) {

        Date start = null, end = null;
        int period, interval;

        String endDateParameter = request.getParameter("endDate");
        if (Objects.nonNull(endDateParameter)) {
            end = new Date(Long.parseLong(endDateParameter));
        }
        String startDateParameter = request.getParameter("startDate");
        if (Objects.nonNull(startDateParameter)) {
            start = new Date(Long.parseLong(startDateParameter));
        }

        String periodParameter = request.getParameter("period");
        String intervalParameter = request.getParameter("interval");
        if (Objects.nonNull(periodParameter) && Objects.nonNull(intervalParameter)) {
            period = Integer.parseInt(periodParameter);
            interval = Integer.parseInt(intervalParameter);
        } else {
            period = Calendar.MINUTE;
            interval = DEFAULT_INTERVAL_OF_MINUTES;
        }

        if (Objects.isNull(start) && Objects.isNull(end)) {
//            return generateChartDataFromBeginningDayToNow();
            return generateDefaultChartData();
        }

        return new ChartData();
    }

    private ChartData generateChartDataFromBeginningDayToNow() {


        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();

        LineChartData lineChartDataContent = new LineChartData();
        lineChartDataContent.setLabel("chart data from beginning day to now by interval of 10 minutes");
        List<Long> dataGraph = new ArrayList<>();

        Date start = DateUtil.generateBeginningOfDay();

        int hoursRange = (int) DateUtil.rangeHoursDate(start) / 60;
        int[] hours = new int[hoursRange];
        for (int h = 24, i = 0 ; i < hoursRange ; h--, i++) {
            hours[i] = h;
            System.out.println("#: " + (i + 1) + ", hour = " + h);
        }
        for (int hour : hours) {
            DateRangeModel dateRange = DateUtil.generateDateRangeByFromAndToLastHour(Calendar.HOUR, hour, DEFAULT_INTERVAL_OF_HOUR);
            lineChartLabels.add(String.valueOf(dateRange.getStart()));
            dataGraph.add(documentRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
        }
        lineChartDataContent.setData(dataGraph);
        lineChartData.add(lineChartDataContent);

        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);

        return chartData;
    }

    private ChartData generateDefaultChartData() {
        Date start = DateUtil.generateBeginningOfDay();
        Date end = DateUtil.addHour(start, 1);
        long hours = DateUtil.rangeHoursDate(start) / 60;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();

        LineChartData lineChartDataContent = new LineChartData();
        lineChartDataContent.setLabel("chart data by today by interval of 1 hour");
        List<Long> dataGraph = new ArrayList<>();
        for (int h = 0 ; h <= hours ; ++h) {
            lineChartLabels.add(dateFormat.format(start));
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
