package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Party {

	private EndpointID endpointID;
	private PartyIdentification partyIdentification;
	private String partyName;
	private PartyLegalEntity partyLegalEntity;
	private Contact contact;

}
