package dk.erst.delis.task.identifier.publish.data;

import lombok.Data;

@Data
public class SmpProcessIdentifier {

	private static String DEFAULT_SCHEME = "cenbii-procid-ubl";

	private String processIdentifierScheme;
	private String processIdentifierValue;

	public static SmpProcessIdentifier of(String processIdentifierValue) {
		SmpProcessIdentifier r = new SmpProcessIdentifier();
		r.setProcessIdentifierValue(processIdentifierValue);
		r.setProcessIdentifierScheme(DEFAULT_SCHEME);
		return r;
	}
}
