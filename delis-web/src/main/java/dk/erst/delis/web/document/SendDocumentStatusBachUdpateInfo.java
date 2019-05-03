package dk.erst.delis.web.document;

import java.util.ArrayList;
import java.util.List;

import dk.erst.delis.data.enums.document.SendDocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendDocumentStatusBachUdpateInfo {
    List<Long> idList = new ArrayList<>();
    SendDocumentStatus status;
}
