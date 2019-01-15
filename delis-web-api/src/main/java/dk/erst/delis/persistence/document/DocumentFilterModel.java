package dk.erst.delis.persistence.document;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
public class DocumentFilterModel extends AbstractFilterModel {

    private String organisation;
    private String receiver;
    private DocumentStatus documentStatus;
    private DocumentErrorCode lastError;
    private String senderName;
    private DocumentFormat ingoingDocumentFormat;
    private DocumentType documentType;
}
