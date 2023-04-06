package dk.erst.delis.document.sbdh.ubl;

import network.oxalis.sniffer.document.PlainUBLParser;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

public class DelisOrderResponseDocumentParser extends DelisOrderDocumentParser {

    public DelisOrderResponseDocumentParser(PlainUBLParser parser)  {
        super(parser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        return super.getReceiver();
    }

    @Override
    public ParticipantIdentifier getReceiver() {
    	return super.getSender();
    }
}
