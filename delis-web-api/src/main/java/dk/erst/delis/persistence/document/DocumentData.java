package dk.erst.delis.persistence.document;

import dk.erst.delis.data.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Getter
@Setter
public class DocumentData {

    private long id;
    private String organisation;
    private String receiver;
    private DocumentStatus status;
    private DocumentErrorCode lastError;
    private DocumentType documentType;
    private DocumentFormat ingoingFormat;
    private String received;
    private String senderName;

    public DocumentData(Document document) {
        this.id = document.getId();
        if (Objects.nonNull(document.getOrganisation())) {
            this.organisation = document.getOrganisation().getName();
        }
        if (Objects.nonNull(document.getReceiverIdentifier())) {
            this.receiver = document.getReceiverIdentifier().getName() + " " + document.getReceiverIdentifier().getUniqueValueType();
        }
        this.status = document.getDocumentStatus();
        this.lastError = document.getLastError();
        this.documentType = document.getIngoingDocumentFormat().getDocumentType();
        this.ingoingFormat = document.getIngoingDocumentFormat();
        this.received = document.getCreateTime().toString();
        this.senderName = document.getSenderName();
    }
}
