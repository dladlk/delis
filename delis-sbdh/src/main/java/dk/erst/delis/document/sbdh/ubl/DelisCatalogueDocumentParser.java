package dk.erst.delis.document.sbdh.ubl;

import network.oxalis.sniffer.document.PlainUBLParser;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

public class DelisCatalogueDocumentParser extends DelisOrderDocumentParser {

    public DelisCatalogueDocumentParser(PlainUBLParser parser)  {
        super(parser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        String endpointFirst = "//cac:ProviderParty/cbc:EndpointID";
        String companySecond = "//cac:ProviderParty/cac:PartyLegalEntity/cbc:CompanyID";
        ParticipantIdentifier s;
        try {
            s = participantId(endpointFirst);
        } catch (IllegalStateException e) {
            s = participantId(companySecond);
        }
        return s;
    }

    @Override
    public ParticipantIdentifier getReceiver() {
        String endpointFirst = "//cac:ReceiverParty/cbc:EndpointID";
        String companySecond = "//cac:ReceiverParty/cac:PartyLegalEntity/cbc:CompanyID";
        ParticipantIdentifier s;
        try {
            s = participantId(endpointFirst);
        } catch (IllegalStateException e) {
            s = participantId(companySecond);
        }
        return s;
    }

}
