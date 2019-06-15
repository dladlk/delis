package dk.erst.delis.rest.data.request.document;

import dk.erst.delis.data.enums.document.DocumentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UpdateDocumentStatusData {

    private DocumentStatus status;
    private List<Long> ids;
}
