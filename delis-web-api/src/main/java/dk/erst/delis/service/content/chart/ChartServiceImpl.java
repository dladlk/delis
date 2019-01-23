package dk.erst.delis.service.content.chart;

import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public ChartData generateChartDataByLastHourFromIntervalOfTenMinutes() {
        // generate chart data by last hour by interval of 10 minutes

        ChartData chartData = new ChartData();
        List<LineChartData> lineChartData = new ArrayList<>();
        List<String> lineChartLabels = new ArrayList<>();

        LineChartData lineChartDataContent = new LineChartData();
        lineChartDataContent.setLabel("chart data by last hour by interval of 10 minutes");
        List<Long> dataGraf = new ArrayList<>();
        int[] minutes = {60, 50, 40, 30, 20, 10};
        for (int minute : minutes) {
            DateRangeModel dateRange = DateUtil.generateDateRangeByFromAndToLastHour(minute, 10);
            lineChartLabels.add(String.valueOf(dateRange.getStart()));
            dataGraf.add(documentRepository.countByCreateTimeBetween(dateRange.getStart(), dateRange.getEnd()));
        }
        lineChartDataContent.setData(dataGraf);
        lineChartData.add(lineChartDataContent);

        chartData.setLineChartData(lineChartData);
        chartData.setLineChartLabels(lineChartLabels);

        return chartData;
    }
}
