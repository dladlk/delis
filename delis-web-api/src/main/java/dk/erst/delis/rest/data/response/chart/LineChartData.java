package dk.erst.delis.rest.data.response.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author funtusthan, created by 23.01.19
 */

@Getter
@Setter
public class LineChartData {

    private List<String> data;
    private String label;
}
