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

	public boolean isMatch(SmpPublishServiceData match) {
		boolean res = false;
		if (this.documentIdentifier != null) {
			res = this.documentIdentifier.isMatch(match.getDocumentIdentifier());
		} else if (match.getDocumentIdentifier() == null) {
			res = true;
		}

		if (res) {
			if (this.processIdentifier != null) {
				res = this.processIdentifier.isMatch(match.getProcessIdentifier());
			} else if (match.getProcessIdentifier() == null) {
				res = true;
			}
		}

		if (res) {
			if (!isEmpty(this.endpoints)) {
				if (isEmpty(match.getEndpoints())) {
					res = false;
				} else {
					for (SmpServiceEndpointData e : this.endpoints) {
						boolean endpointMatched = false;
						for (SmpServiceEndpointData em : match.getEndpoints()) {
							if (e.isMatch(em)) {
								endpointMatched = true;
								break;
							}
						}
						if (!endpointMatched) {
							res = false;
							break;
						}
					}
				}
			} else {
				res = isEmpty(match.getEndpoints());
			}
		}

		return res;
	}

	private static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	public static String byProcessIdentifierScheme(SmpPublishServiceData d) {
		return d.getProcessIdentifier().getProcessIdentifierScheme();
	}

	public static String byProcessIdentifierValue(SmpPublishServiceData d) {
		return d.getProcessIdentifier().getProcessIdentifierValue();
	}

	public static String byDocumentIdentifierScheme(SmpPublishServiceData d) {
		return d.getDocumentIdentifier().getDocumentIdentifierScheme();
	}

	public static String byDocumentIdentifierValue(SmpPublishServiceData d) {
		return d.getDocumentIdentifier().getDocumentIdentifierValue();
	}

}
