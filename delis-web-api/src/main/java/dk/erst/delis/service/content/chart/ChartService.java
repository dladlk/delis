package dk.erst.delis.service.content.chart;

import dk.erst.delis.rest.data.response.chart.ChartData;

import org.springframework.web.context.request.WebRequest;

public interface ChartService {

    ChartData generateChartData(WebRequest request);
}
