package dk.erst.delis.document.sbdh.ubl;

import network.oxalis.sniffer.document.PlainUBLParser;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

public class DelisApplicationResponseDocumentParser extends DelisAbstractDocumentParser {

	public DelisApplicationResponseDocumentParser(PlainUBLParser parser) {
		super(parser);
	}

	@Override
	public ParticipantIdentifier getSender() {
		String applicationResponse = "//cac:SenderParty/cbc:EndpointID";
		return participantId(applicationResponse);
	}

	@Override
	public ParticipantIdentifier getReceiver() {
		String applicationResponse = "//cac:ReceiverParty/cbc:EndpointID";
		return participantId(applicationResponse);
	}
}
