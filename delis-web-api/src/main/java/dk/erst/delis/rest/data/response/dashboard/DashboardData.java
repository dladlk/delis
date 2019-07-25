package dk.erst.delis.rest.data.response.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardData {

    private long identifierLastHour;
    private long errorLastHour;
    private long receivedDocumentsLastHour;
    private long averageDocumentsLastHour;
    private long journalDocument;
    private long journalIdentifier;
    private long journalOrganisation;
}
