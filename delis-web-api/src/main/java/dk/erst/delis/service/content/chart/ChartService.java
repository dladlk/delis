package dk.erst.delis.service.content.chart;

import dk.erst.delis.rest.data.response.chart.ChartData;

/**
 * @author funtusthan, created by 23.01.19
 */

public interface ChartService {

    ChartData generateChartDataByLastHourFromIntervalOfTenMinutes();
}
