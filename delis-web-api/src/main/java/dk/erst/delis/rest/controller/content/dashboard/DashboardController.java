package dk.erst.delis.rest.controller.content.dashboard;

import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.dashboard.DashboardData;
import dk.erst.delis.service.content.dashboard.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DataContainer<DashboardData>> generateDashboardData() {
        return ResponseEntity.ok(new DataContainer<>(dashboardService.generateDashboardData()));
    }
}
