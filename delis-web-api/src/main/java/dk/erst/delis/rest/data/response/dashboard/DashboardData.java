package dk.erst.delis.rest.data.response.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardData {

    private long identifierLastHour;
    private long errorLastHour;
    private long receivedDocumentsLastHour;
    private long sendDocumentsLastHour;
    private long averageDocumentsLastHour;
    private long journalDocument;
    private long journalIdentifier;
    private long journalOrganisation;

    public DashboardData() {
        this.identifierLastHour = 0;
        this.errorLastHour = 0;
        this.receivedDocumentsLastHour = 0;
        this.sendDocumentsLastHour = 0;
        this.averageDocumentsLastHour = 0;
        this.journalDocument = 0;
        this.journalIdentifier = 0;
        this.journalOrganisation = 0;
    }
}
