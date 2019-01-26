package dk.erst.delis.rest.controller.content.chart;

import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.service.content.chart.ChartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 23.01.19
 */

@RestController
@RequestMapping("/rest/chart")
public class ChartController {

    private final ChartService chartService;

    @Autowired
    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping
    public ResponseEntity<DataContainer<ChartData>> generateDashboardData(WebRequest request) {
        return ResponseEntity.ok(new DataContainer<>(chartService.generateChartData(request)));
    }
}
