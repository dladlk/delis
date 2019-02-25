package dk.erst.delis.task.identifier.publish.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmpDocumentProcessData {

	private SmpDocumentIdentifier documentIdentifier;
	private SmpProcessIdentifier processIdentifier;

}
