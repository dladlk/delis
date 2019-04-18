package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Party {

	private EndpointID endpointID;
	private PartyIdentification partyIdentification;
	private PartyName partyName;
	private PartyLegalEntity partyLegalEntity;
	private Contact contact;

}
