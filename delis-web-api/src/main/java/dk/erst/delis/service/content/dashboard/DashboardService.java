package dk.erst.delis.service.content.dashboard;

import dk.erst.delis.rest.data.response.dashboard.DashboardDocumentData;
import dk.erst.delis.rest.data.response.dashboard.DashboardSendDocumentData;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

public interface DashboardService {

    List<DashboardDocumentData> generateDashboardDocumentDataList(WebRequest request);
    List<DashboardDocumentData> generateDashboardDocumentErrorDataList(WebRequest request);
    List<DashboardSendDocumentData> generateSendDashboardDocumentDataList(WebRequest request);
}
