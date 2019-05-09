package dk.erst.delis.config.codelist;

import lombok.Data;

@Data
public class ParticipantIdentifierScheme {

	private String schemeID;
	private String icdValue;
	private String issuingOrganization;
}
