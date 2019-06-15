package dk.erst.delis.rest.data.response.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author funtusthan, created by 23.01.19
 */

@Getter
@Setter
public class ChartData {

    private List<LineChartData> lineChartData;
    private List<String> lineChartLabels;

    public ChartData() {
        this.lineChartData = Collections.emptyList();
        this.lineChartLabels = Collections.emptyList();
    }
}
