package dk.erst.delis.persistence.document;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.DocumentType;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
public class DocumentFilterModel {

    String organisation = null;
    String receiver = null;
    List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.values());
    List<DocumentErrorCode> lastErrors = Arrays.asList(DocumentErrorCode.values());
    String senderName = null;
    List<DocumentFormat> documentFormats = Arrays.asList(DocumentFormat.values());
    List<DocumentType> documentTypes = Arrays.asList(DocumentType.values());
    Date start = null;
    Date end = null;
}

