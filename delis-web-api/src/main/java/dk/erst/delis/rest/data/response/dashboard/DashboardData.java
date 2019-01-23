package dk.erst.delis.rest.data.response.dashboard;

import dk.erst.delis.rest.data.response.chart.ChartData;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 22.01.19
 */

@Getter
@Setter
public class DashboardData {

    private long publishedLastHour;
    private long errorLastHour;
    private long receivedDocumentsLastHour;
    private long averageDocumentsLastHour;
    private long journalDocument;
    private long journalIdentifier;
    private long journalOrganisation;
    private ChartData chartData;
}
