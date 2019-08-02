package dk.erst.delis.rest.data.response.chart;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineChartData {

	private List<Long> data;
	private String label;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(label);
		sb.append(": ");
		sb.append(data);
		return sb.toString();
	}
}
