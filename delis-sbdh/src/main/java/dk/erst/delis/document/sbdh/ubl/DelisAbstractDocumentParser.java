package dk.erst.delis.document.sbdh.ubl;

import org.w3c.dom.Element;

import no.difi.oxalis.sniffer.document.PlainUBLParser;
import no.difi.oxalis.sniffer.document.parsers.AbstractDocumentParser;
import no.difi.oxalis.sniffer.identifier.ParticipantId;
import no.difi.oxalis.sniffer.identifier.SchemeId;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.icd.api.Icd;

public abstract class DelisAbstractDocumentParser extends AbstractDocumentParser {

    public DelisAbstractDocumentParser(PlainUBLParser parser) {
        super(parser);
    }

    /**
     * Retrieves the ParticipantId which is retrieved using the supplied XPath.
     */
    protected ParticipantIdentifier participantId(String xPathExpr) {
        ParticipantId ret;

        // first we retrieve the correct participant element
        Element element;
        try {
            element = parser.retrieveElementForXpath(xPathExpr);
        } catch (Exception ex) {
            // DOM parser throws "java.lang.IllegalStateException: No element in XPath: ..." if no Element is found
            throw new IllegalStateException(String.format("No ParticipantId found at '%s'.", xPathExpr));
        }

        // get value and any schemeId given
        String companyId = element.getFirstChild().getNodeValue().trim();
        String schemeIdTextValue = element.getAttribute("schemeID").trim();

        // check if we already have a valid participant 9908:987654321
        if (ParticipantId.isValidParticipantIdentifierPattern(companyId)) {
            if (schemeIdTextValue.length() == 0) {
                // we accept participants with icd prefix if schemeId is missing ...
                ret = new ParticipantId(companyId);
            } else {
                // ... or when given schemeId matches the icd code stat eg NO:VAT matches 9908 from 9908:987654321
                if (companyId.startsWith(SchemeId.parse(schemeIdTextValue).getCode() + ":")) {
                    ret = new ParticipantId(companyId);
                } else {
                    throw new IllegalStateException(String.format(
                            "ParticipantId at '%s' is illegal, schemeId '%s' and icd code prefix of '%s' does not match",
                            xPathExpr, schemeIdTextValue, companyId));
                }
            }
        } else {
            // try to add the given icd prefix to the participant id
        	
        	Icd icd;
        	try {
        		icd = SchemeId.parse(schemeIdTextValue);
        	} catch (Exception e) {
        		icd = SchemeId.fromISO6523(schemeIdTextValue);
        	}
        	
            companyId = String.format("%s:%s", icd.getCode(), companyId);
            if (!ParticipantId.isValidParticipantIdentifierPattern(companyId)) {
                throw new IllegalStateException(String.format(
                        "ParticipantId syntax at '%s' evaluates to '%s' and is invalid", xPathExpr, companyId));
            }
            ret = new ParticipantId(companyId);
        }
        return ret.toVefa();
    }

}
