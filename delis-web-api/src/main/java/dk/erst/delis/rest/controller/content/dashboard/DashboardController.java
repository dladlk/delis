package dk.erst.delis.rest.controller.content.dashboard;

import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.service.content.dashboard.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/rest/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/document")
    public ResponseEntity generateDashboardDocumentData(WebRequest request) {
        return ResponseEntity.ok(new ListContainer<>(dashboardService.generateDashboardDocumentDataList(request)));
    }

    @GetMapping("/document/error")
    public ResponseEntity generateDashboardDocumentErrorData(WebRequest request) {
        return ResponseEntity.ok(new ListContainer<>(dashboardService.generateDashboardDocumentErrorDataList(request)));
    }

    @GetMapping("/send")
    public ResponseEntity generateDashboardSendDocumentData(WebRequest request) {
        return ResponseEntity.ok(new ListContainer<>(dashboardService.generateSendDashboardDocumentDataList(request)));
    }
}
