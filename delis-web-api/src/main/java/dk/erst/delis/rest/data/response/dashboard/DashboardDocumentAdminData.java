package dk.erst.delis.rest.data.response.dashboard;

import dk.erst.delis.data.entities.document.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DashboardDocumentAdminData extends DashboardDocumentData {

    private String organisation;
    private List<String> receiverIdentifierNames;

    public DashboardDocumentAdminData(String organisation) {
        super();
        this.organisation = organisation;
    }

    public DashboardDocumentAdminData(String organisation, List<Document> documents, String locale) {
        super(documents, locale);
        this.organisation = organisation;
        this.receiverIdentifierNames = documents.stream().map(document -> document.getReceiverIdentifier().getName()).distinct().collect(Collectors.toList());
    }
}
