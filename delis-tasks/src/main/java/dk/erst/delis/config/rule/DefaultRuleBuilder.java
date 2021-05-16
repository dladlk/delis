package dk.erst.delis.config.rule;

import static dk.erst.delis.data.enums.document.DocumentFormatFamily.BIS3;
import static dk.erst.delis.data.enums.document.DocumentFormatFamily.CII;
import static dk.erst.delis.data.enums.document.DocumentFormatFamily.OIOUBL;

import java.util.ArrayList;
import java.util.List;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;

public class DefaultRuleBuilder {
	
	private static final String CIUS_VERSION = "2021-05-17_v1.7.0";
	private static final String PEPPOL_BIS3_OTHER_VERSION = "2021-05-17_v1.2.2";

    public static List<RuleDocumentTransformation> buildDefaultTransformationRuleList() {
        ArrayList<RuleDocumentTransformation> result = new ArrayList<>();
        result.add(b(CII, BIS3, "cius/" + CIUS_VERSION + "/CII_2_BIS3/CII_2_BIS-Billing.xslt"));
        result.add(b(BIS3, OIOUBL, "cius/" + CIUS_VERSION + "/BIS3_2_OIOUBL/BIS-Billing_2_OIOUBL_MASTER.xslt"));
        return result;
    }
    
    private static RuleDocumentTransformation b(DocumentFormatFamily from, DocumentFormatFamily to, String path) {
        RuleDocumentTransformation r = new RuleDocumentTransformation();

        r.setActive(true);
        r.setDocumentFormatFamilyFrom(from);
        r.setDocumentFormatFamilyTo(to);
        r.setRootPath(path);

        return r;
    }
    
    public static List<RuleDocumentValidation> buildDefaultValidationRuleList() {

        List<RuleDocumentValidation> result = new ArrayList<>();
        /*
         * XSD rules
         */
        result.add(xsd(DocumentFormat.OIOUBL_INVOICE, "xsd/UBL_2.0/maindoc/UBL-Invoice-2.0.xsd"));
        result.add(xsd(DocumentFormat.OIOUBL_CREDITNOTE, "xsd/UBL_2.0/maindoc/UBL-CreditNote-2.0.xsd"));

        result.add(xsd(DocumentFormat.BIS3_INVOICE, "xsd/UBL_2.1/maindoc/UBL-Invoice-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_CREDITNOTE, "xsd/UBL_2.1/maindoc/UBL-CreditNote-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_INVOICE_RESPONSE, "xsd/UBL_2.1/maindoc/UBL-ApplicationResponse-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE, "xsd/UBL_2.1/maindoc/UBL-ApplicationResponse-2.1.xsd"));

        result.add(xsd(DocumentFormat.BIS3_ORDER,       "xsd/UBL_2.1/maindoc/UBL-Order-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_ORDER_ONLY,  "xsd/UBL_2.1/maindoc/UBL-Order-2.1.xsd"));

        result.add(xsd(DocumentFormat.BIS3_ORDER_RESPONSE, "xsd/UBL_2.1/maindoc/UBL-OrderResponse-2.1.xsd"));
        
        result.add(xsd(DocumentFormat.BIS3_CATALOGUE_ONLY, "xsd/UBL_2.1/maindoc/UBL-Catalogue-2.1.xsd"));
        result.add(xsd(DocumentFormat.BIS3_CATALOGUE_RESPONSE, "xsd/UBL_2.1/maindoc/UBL-ApplicationResponse-2.1.xsd"));

        result.add(xsd(DocumentFormat.CII, "xsd/CII_D16B_SCRDM_uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));

        /*
         * Schematron
         */

		result.add(sch(DocumentFormat.OIOUBL_INVOICE, "sch/oioubl/OIOUBL_Schematron_2019-04-08_v1.11.1.35666/OIOUBL_Invoice_Schematron.xsl", 10));
		result.add(sch(DocumentFormat.OIOUBL_CREDITNOTE, "sch/oioubl/OIOUBL_Schematron_2019-04-08_v1.11.1.35666/OIOUBL_CreditNote_Schematron.xsl", 10));

		
		/*
		 * Schematron files for CEN BIS3/CII are generated basing on next GitHub project:
		 * 
		 * https://github.com/OpenPEPPOL/peppol-bis-invoice-3/tree/master/rules/sch
		 * 
		 * 
		 * CEN part of these schematron files are actaully copied from another GitHub project:
		 * 
		 * https://github.com/CenPC434/validation
		 *
		 * 2020-11-11 Schematron files for InvoiceResponse, MRL, Catalogue, Catalogue Response - taken from https://www.digitaliser.dk/resource/5795845
		 * 
		 */
		
		String cius = "sch/cius/" + CIUS_VERSION;
		String peppol_bis3_other = "sch/bis3/other/" + PEPPOL_BIS3_OTHER_VERSION;

		String BIS3_PEPPOL = cius + "/PEPPOL-EN16931-UBL.xslt";
		String BIS3_CEN = cius + "/CEN-EN16931-UBL.xslt";
		
		result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_CEN, 10));
		result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_PEPPOL, 20));
		result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_CEN, 10));
		result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_PEPPOL, 20));

		result.add(sch(DocumentFormat.BIS3_INVOICE_RESPONSE, peppol_bis3_other + "/PEPPOLBIS-T111.xslt", 10));
		result.add(sch(DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE, peppol_bis3_other + "/PEPPOLBIS-T71.xslt", 10));
		result.add(sch(DocumentFormat.BIS3_CATALOGUE_ONLY, peppol_bis3_other + "/PEPPOLBIS-T19.xslt", 10));
		result.add(sch(DocumentFormat.BIS3_CATALOGUE_RESPONSE, peppol_bis3_other + "/PEPPOLBIS-T58.xslt", 10));

		result.add(sch(DocumentFormat.CII, cius + "/CEN-EN16931-CII.xslt", 10));
		result.add(sch(DocumentFormat.CII, cius + "/PEPPOL-EN16931-CII.xslt", 20));
		return result;
	}

    private static RuleDocumentValidation xsd(DocumentFormat format, String path) {
        RuleDocumentValidation v = new RuleDocumentValidation();
        v.setActive(true);
        v.setDocumentFormat(format);
        v.setPriority(10);
        v.setValidationType(RuleDocumentValidationType.XSD);
        v.setRootPath(path);
        return v;
    }

    private static RuleDocumentValidation sch(DocumentFormat format, String path, int priority) {
        RuleDocumentValidation v = new RuleDocumentValidation();
        v.setActive(true);
        v.setDocumentFormat(format);
        v.setPriority(priority);
        v.setValidationType(RuleDocumentValidationType.SCHEMATRON);
        v.setRootPath(path);
        return v;
    }
}
