package dk.erst.delis.task.identifier.publish.data;

import java.util.List;

import lombok.Data;

@Data
public class SmpPublishServiceData {

	private SmpDocumentIdentifier documentIdentifier;

	private SmpProcessIdentifier processIdentifier;

	private List<SmpServiceEndpointData> endpoints;

	public String buildServiceId() {
		StringBuilder sb = new StringBuilder();
		sb.append(documentIdentifier.getDocumentIdentifierScheme());
		sb.append("::");
		sb.append(documentIdentifier.getDocumentIdentifierValue());
		return sb.toString();
	}

}
