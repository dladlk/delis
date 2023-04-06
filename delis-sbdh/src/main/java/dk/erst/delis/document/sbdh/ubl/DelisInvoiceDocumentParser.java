package dk.erst.delis.document.sbdh.ubl;

import network.oxalis.sniffer.document.PlainUBLParser;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

public class DelisInvoiceDocumentParser extends DelisAbstractDocumentParser {

    public DelisInvoiceDocumentParser(PlainUBLParser parser)  {
        super(parser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        String endpointFirst = "//cac:AccountingSupplierParty/cac:Party/cbc:EndpointID";
        String companySecond = "//cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
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
        String endpointFirst = "//cac:AccountingCustomerParty/cac:Party/cbc:EndpointID";
        String companySecond = "//cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
        ParticipantIdentifier s;
        try {
            s = participantId(endpointFirst);
        } catch (IllegalStateException e) {
            s = participantId(companySecond);
        }
        return s;
    }
}
