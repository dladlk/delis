package dk.erst.delis.service.content.chart;

import dk.erst.delis.rest.data.response.chart.ChartData;

import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 23.01.19
 */

public interface ChartService {

    ChartData generateChartData(WebRequest request);
}
