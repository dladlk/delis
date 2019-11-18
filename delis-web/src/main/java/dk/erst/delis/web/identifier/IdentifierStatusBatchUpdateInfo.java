package dk.erst.delis.web.identifier;

import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
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
public class IdentifierStatusBatchUpdateInfo {
    private List<Long> idList = new ArrayList<>();
    private IdentifierStatus status;
    private IdentifierPublishingStatus publishStatus;
}
