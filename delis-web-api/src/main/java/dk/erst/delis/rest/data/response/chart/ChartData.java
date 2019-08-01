package dk.erst.delis.rest.data.response.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ChartData {

    private List<LineChartData> lineChartData;
    private List<String> lineChartLabels;

    public ChartData() {
        this.lineChartData = Collections.emptyList();
        this.lineChartLabels = Collections.emptyList();
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
		sb.append(lineChartLabels);
		sb.append("\t");
		sb.append(lineChartData);
		return sb.toString();
    }
}
