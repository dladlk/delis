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

    public static List<RuleDocumentTransformation> buildDefaultTransformationRuleList() {
        ArrayList<RuleDocumentTransformation> result = new ArrayList<>();
        result.add(b(CII, BIS3, "cii_to_bis3/v_2018-12-22_DLK_Change_AddressLine_PayableRoundingAmount/CII_2_BIS-Billing.xslt"));
        result.add(b(BIS3, OIOUBL, "bis3_to_oioubl/v_2018-03-14_34841/BIS-Billing_2_OIOUBL_MASTER.xslt"));
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

        result.add(xsd(DocumentFormat.CII, "xsd/CII_D16B_SCRDM_uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));

        /*
         * Schematron
         */

        result.add(sch(DocumentFormat.OIOUBL_INVOICE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_Invoice_Schematron.xsl", 10));
        result.add(sch(DocumentFormat.OIOUBL_CREDITNOTE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_CreditNote_Schematron.xsl", 10));

        String BIS3_PEPPOL = "sch/bis3/peppol_2019-01-02_1/PEPPOL-EN16931-UBL.xslt";
        String BIS3_CEN = "sch/bis3/cen_2019-01-02_1/CEN-EN16931-UBL.xslt";
        result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_CEN, 10));
        result.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_PEPPOL, 20));
        result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_CEN, 10));
        result.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_PEPPOL, 20));

        result.add(sch(DocumentFormat.CII, "sch/cii/cen_2019-01-02_1/CEN-EN16931-CII.xslt", 10));
        result.add(sch(DocumentFormat.CII, "sch/cii/peppol_2019-01-02_1/PEPPOL-EN16931-CII.xslt", 20));
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
