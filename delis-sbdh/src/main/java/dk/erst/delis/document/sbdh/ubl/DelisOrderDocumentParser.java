package dk.erst.delis.document.sbdh.ubl;

import no.difi.oxalis.sniffer.document.PlainUBLParser;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

public class DelisOrderDocumentParser extends DelisAbstractDocumentParser {

    public DelisOrderDocumentParser(PlainUBLParser parser)  {
        super(parser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        String endpointFirst = "//cac:BuyerCustomerParty/cac:Party/cbc:EndpointID";
        String companySecond = "//cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
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
        String endpointFirst = "//cac:SellerSupplierParty/cac:Party/cbc:EndpointID";
        String companySecond = "//cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
        ParticipantIdentifier s;
        try {
            s = participantId(endpointFirst);
        } catch (IllegalStateException e) {
            s = participantId(companySecond);
        }
        return s;
    }
}
