package dk.erst.delis.rest.data.response.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LineChartData {

    private List<Long> data;
    private String label;
}
