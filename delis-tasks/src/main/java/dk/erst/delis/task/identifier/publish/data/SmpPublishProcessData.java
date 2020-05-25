package dk.erst.delis.task.identifier.publish.data;

import java.util.List;

import lombok.Data;

@Data
public class SmpPublishProcessData {

	private SmpProcessIdentifier processIdentifier;

	private List<SmpServiceEndpointData> endpoints;

	public boolean isMatch(SmpPublishProcessData match) {
		boolean res = false;
		if (this.processIdentifier != null) {
			res = this.processIdentifier.isMatch(match.getProcessIdentifier());
		} else if (match.getProcessIdentifier() == null) {
			res = true;
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

}
