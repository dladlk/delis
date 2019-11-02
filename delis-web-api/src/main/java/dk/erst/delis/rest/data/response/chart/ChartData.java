package dk.erst.delis.rest.data.response.chart;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChartData {

    private List<LineChartData> lineChartData;
    private List<String> lineChartLabels;
    
    @Setter
    private int deliveryAlertCount;

    public ChartData() {
        this.lineChartData = new ArrayList<LineChartData>();
        this.lineChartLabels = new ArrayList<String>();
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("deliveryAlertCount=");
    	sb.append(this.deliveryAlertCount);
    	sb.append("\t");
		sb.append(lineChartLabels);
		sb.append("\t");
		sb.append(lineChartData);
		return sb.toString();
    }
    
    public void addLineChart(LineChartData lcd) {
    	this.lineChartData.add(lcd);
    }
    public void addLineChartLabel(String dataLabel) {
    	this.lineChartLabels.add(dataLabel);
    }
    

}
