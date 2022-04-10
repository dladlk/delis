package dk.erst.delis.document.sbdh.cii;

import network.oxalis.sniffer.document.PlainUBLHeaderParser;
import network.oxalis.sniffer.document.parsers.AbstractDocumentParser;
import network.oxalis.sniffer.identifier.ParticipantId;
import network.oxalis.sniffer.identifier.SchemeId;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.icd.api.Icd;
import org.apache.commons.lang.math.NumberUtils;

public class CIIDocumentParser extends AbstractDocumentParser {

    public CIIDocumentParser(PlainUBLHeaderParser ublHeaderParser) {
        super(ublHeaderParser);
    }

    @Override
    public ParticipantIdentifier getSender() {
        String schemeID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID/@schemeID");
        String globalID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID");
        return getParticipantIdentifier(schemeID, globalID);
    }

    @Override
    public ParticipantIdentifier getReceiver() {
        String schemeID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID");
        String globalID = parser.retriveValueForXpath("//rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID");
        return getParticipantIdentifier(schemeID, globalID);
    }

    private ParticipantIdentifier getParticipantIdentifier(String schemeID, String globalID) {
        Icd icd;
        if(NumberUtils.isDigits(schemeID)) {
            icd = SchemeId.fromISO6523(schemeID);
        } else {
            icd = SchemeId.parse(schemeID);
        }
        return new ParticipantId(icd, globalID).toVefa();
    }
}
