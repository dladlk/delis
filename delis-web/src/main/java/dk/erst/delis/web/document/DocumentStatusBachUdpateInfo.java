package dk.erst.delis.web.document;

import dk.erst.delis.data.enums.document.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentStatusBachUdpateInfo {
    List<Long> idList = new ArrayList<>();
    DocumentStatus status;
}
