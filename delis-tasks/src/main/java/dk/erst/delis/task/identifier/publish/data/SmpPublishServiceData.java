package dk.erst.delis.task.identifier.publish.data;

import java.util.List;

import lombok.Data;

@Data
public class SmpPublishServiceData {

	private SmpDocumentIdentifier documentIdentifier;
	
	private List<SmpPublishProcessData> processList;

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
			if (!isEmpty(this.processList)) {
				if (isEmpty(match.processList)) {
					res = false;
				} else {
					for (SmpPublishProcessData e : this.processList) {
						boolean endpointMatched = false;
						for (SmpPublishProcessData em : match.processList) {
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
				res = isEmpty(match.processList);
			}
		}

		return res;
	}
	
	private static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

}
