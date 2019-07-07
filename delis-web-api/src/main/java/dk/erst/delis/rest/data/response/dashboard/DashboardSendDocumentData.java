package dk.erst.delis.rest.data.response.dashboard;

import dk.erst.delis.data.entities.document.SendDocument;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DashboardSendDocumentData {

    private long locked;
    private long notLocked;
    private long countSendDocuments;
    private String organisation;
    private List<String> documentStatuses;
    private List<String> documentTypes;
    private List<String> receiverIdRaws;
    private List<String> senderIdRaws;
    private List<String> documentsId;

    private DashboardSendDocumentData() {
        this.countSendDocuments = 0;
        this.locked = 0;
        this.notLocked = 0;
        this.documentStatuses = Collections.emptyList();
        this.documentTypes = Collections.emptyList();
        this.receiverIdRaws = Collections.emptyList();
        this.senderIdRaws = Collections.emptyList();
        this.documentsId = Collections.emptyList();
    }

    public DashboardSendDocumentData(String organisation) {
        this();
        this.organisation = organisation;
    }

    public DashboardSendDocumentData(String organisation, List<SendDocument> documents, String locale) {
        this.organisation = organisation;
        this.countSendDocuments = documents.size();
        this.locked = documents.stream().filter(SendDocument::isLocked).count();
        this.notLocked = documents.stream().filter(document -> !document.isLocked()).count();
        this.receiverIdRaws = documents.stream().map(SendDocument::getReceiverIdRaw).distinct().collect(Collectors.toList());
        this.senderIdRaws = documents.stream().map(SendDocument::getSenderIdRaw).distinct().collect(Collectors.toList());
        this.documentsId = documents.stream().map(SendDocument::getDocumentId).distinct().collect(Collectors.toList());
        if (StringUtils.equals(locale, "en")) {
            this.documentStatuses = documents.stream().map(document -> document.getDocumentStatus().getName()).distinct().collect(Collectors.toList());
            this.documentTypes = documents.stream().map(document -> document.getDocumentType().getName()).distinct().collect(Collectors.toList());
        } else {
            this.documentStatuses = documents.stream().map(document -> document.getDocumentStatus().getNameDa()).distinct().collect(Collectors.toList());
            this.documentTypes = documents.stream().map(document -> document.getDocumentType().getNameDa()).distinct().collect(Collectors.toList());
        }
    }
}
