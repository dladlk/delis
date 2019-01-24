package dk.erst.delis.document.sbdh.cii;

import no.difi.oxalis.sniffer.document.PlainUBLHeaderParser;
import no.difi.oxalis.sniffer.document.parsers.AbstractDocumentParser;
import no.difi.oxalis.sniffer.identifier.ParticipantId;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

public class CIIDocumentParser extends AbstractDocumentParser {

    public CIIDocumentParser(PlainUBLHeaderParser ublHeaderParser) {
        super(ublHeaderParser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        String schemeID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID/@schemeID");
        String globalID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID");
        String companyId = String.format("%s:%s", schemeID, globalID);
        return new ParticipantId(companyId).toVefa();
    }

    @Override
    public ParticipantIdentifier getReceiver() {
        String schemeID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID");
        String globalID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID");
        String companyId = String.format("%s:%s", schemeID, globalID);
        return new ParticipantId(companyId).toVefa();
    }
}
