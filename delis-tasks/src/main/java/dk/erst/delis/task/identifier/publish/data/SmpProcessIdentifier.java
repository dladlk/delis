package dk.erst.delis.task.identifier.publish.data;

import org.apache.commons.codec.binary.StringUtils;

import lombok.Data;

@Data
public class SmpProcessIdentifier {

	private String processIdentifierScheme;
	private String processIdentifierValue;

	public static SmpProcessIdentifier of(String processIdentifierValue, String schemeValue) {
		SmpProcessIdentifier r = new SmpProcessIdentifier();
		r.setProcessIdentifierValue(processIdentifierValue);
		r.setProcessIdentifierScheme(schemeValue);
		return r;
	}
	
	public boolean isMatch(SmpProcessIdentifier match) {
		if (match != null) {
			boolean res = StringUtils.equals(this.processIdentifierScheme, match.getProcessIdentifierScheme());
			if (res) {
				res = StringUtils.equals(this.processIdentifierValue, match.getProcessIdentifierValue());
			}
			return res;
		}
		return false;
	}
}
