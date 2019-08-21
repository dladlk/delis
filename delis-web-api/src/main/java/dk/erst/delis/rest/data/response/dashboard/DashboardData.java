package dk.erst.delis.rest.data.response.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardData {

    private long receivedDocumentsLastHour;
    private long sendDocumentsLastHour;
    private long errorLastHour;

    public DashboardData() {
        this.errorLastHour = 0;
        this.receivedDocumentsLastHour = 0;
        this.sendDocumentsLastHour = 0;
    }
}
