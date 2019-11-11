package dk.erst.delis.task.identifier.publish.data;

import org.apache.commons.codec.binary.StringUtils;

import lombok.Data;

@Data
public class SmpDocumentIdentifier {

	private static final String DEFAULT_SCHEME = "busdox-docid-qns";

	private String documentIdentifierScheme;
	private String documentIdentifierValue;

	public static SmpDocumentIdentifier of(String documentIdentifierValue) {
		SmpDocumentIdentifier d = new SmpDocumentIdentifier();
		d.setDocumentIdentifierValue(documentIdentifierValue);
		d.setDocumentIdentifierScheme(DEFAULT_SCHEME);
		return d;
	}
	
	public boolean isMatch(SmpDocumentIdentifier match) {
		if (match != null) {
			boolean res = StringUtils.equals(this.documentIdentifierScheme, match.getDocumentIdentifierScheme());
			if (res) {
				res = StringUtils.equals(this.documentIdentifierValue, match.getDocumentIdentifierValue());
			}
			return res;
		}
		return false;
	}
}
