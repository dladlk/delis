package dk.erst.delis.rest.data.response.dashboard;

import dk.erst.delis.data.entities.document.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDocumentUserData extends DashboardDocumentData {

    private String receiverIdentifierName;

    public DashboardDocumentUserData(String receiverIdentifierName) {
        super();
        this.receiverIdentifierName = receiverIdentifierName;
    }

    public DashboardDocumentUserData(String receiverIdentifierName, List<Document> documents, String locale) {
        super(documents, locale);
        this.receiverIdentifierName = receiverIdentifierName;
    }
}
