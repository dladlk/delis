package dk.erst.delis.rest.data.response.dashboard;

import dk.erst.delis.data.entities.document.Document;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DashboardDocumentData {

    private long countDocuments;
    private List<String> documentStatuses;
    private List<String> ingoingDocumentFormats;
    private List<String> documentTypes;
    private List<String> receiverIdRaws;
    private List<String> receiverNames;
    private List<String> receiverCountries;
    private List<String> senderIdRaws;
    private List<String> senderNames;
    private List<String> senderCountries;
    private List<String> documentsId;

    DashboardDocumentData() {
        this.countDocuments = 0;
        this.documentStatuses = Collections.emptyList();
        this.ingoingDocumentFormats = Collections.emptyList();
        this.documentTypes = Collections.emptyList();
        this.receiverIdRaws = Collections.emptyList();
        this.receiverNames = Collections.emptyList();
        this.receiverCountries = Collections.emptyList();
        this.senderIdRaws = Collections.emptyList();
        this.senderNames = Collections.emptyList();
        this.senderCountries = Collections.emptyList();
        this.documentsId = Collections.emptyList();
    }

    DashboardDocumentData(List<Document> documents, String locale) {
        this.countDocuments = documents.size();
        this.receiverIdRaws = documents.stream().map(Document::getReceiverIdRaw).distinct().collect(Collectors.toList());
        this.receiverNames = documents.stream().map(Document::getReceiverName).distinct().collect(Collectors.toList());
        this.receiverCountries = documents.stream().map(Document::getReceiverCountry).distinct().collect(Collectors.toList());
        this.senderIdRaws = documents.stream().map(Document::getSenderIdRaw).distinct().collect(Collectors.toList());
        this.senderNames = documents.stream().map(Document::getSenderName).distinct().collect(Collectors.toList());
        this.senderCountries = documents.stream().map(Document::getSenderCountry).distinct().collect(Collectors.toList());
        this.documentsId = documents.stream().map(Document::getDocumentId).distinct().collect(Collectors.toList());
        if (StringUtils.equals(locale, "en")) {
            this.documentStatuses = documents.stream().map(document -> document.getDocumentStatus().getName()).distinct().collect(Collectors.toList());
            this.documentTypes = documents.stream().map(document -> document.getDocumentType().getName()).distinct().collect(Collectors.toList());
            this.ingoingDocumentFormats = documents.stream().map(document -> document.getIngoingDocumentFormat().getName()).distinct().collect(Collectors.toList());
        } else {
            this.documentStatuses = documents.stream().map(document -> document.getDocumentStatus().getNameDa()).distinct().collect(Collectors.toList());
            this.documentTypes = documents.stream().map(document -> document.getDocumentType().getNameDa()).distinct().collect(Collectors.toList());
            this.ingoingDocumentFormats = documents.stream().map(document -> document.getIngoingDocumentFormat().getNameDa()).distinct().collect(Collectors.toList());
        }
    }
}
