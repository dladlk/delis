package dk.erst.delis.data.constants;

public class DocumentFormatConst {

	public static final String NS_CII ="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100";
	public static final String NS_UBL_CREDITNOTE ="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2";
	public static final String NS_UBL_INVOICE ="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
	public static final String NS_UBL_APPLICATION_RESPONSE ="urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2";
	
	public static final String NS_UBL_ORDER ="urn:oasis:names:specification:ubl:schema:xsd:Order-2";
	public static final String NS_UBL_ORDER_RESPONSE ="urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2";
	public static final String NS_UBL_CATALOGUE ="urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2";
	public static final String NS_UBL_CATALOGUE_RESPONSE = NS_UBL_APPLICATION_RESPONSE;
	
	/*
	 * 2019-08-21 During testing 4 test documents, received by OleMad from EU to test integration with regions, 
	 * 
	 * it was found out that EU's examples have BIS3 Invoices with CustomizationID only as "urn:cen.eu:en16931:2017" - 
	 * 
	 * without #compliant part and further.
	 * 
	 * It was decided to resolve such documents as BIS3 too.
	 */
//	public static final String CUSTOMIZATION_BIS3_STARTS_WITH = "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3";
	public static final String CUSTOMIZATION_BIS3_STARTS_WITH = "urn:cen.eu:en16931:2017";
	public static final String CUSTOMIZATION_OIOUBL = "OIOUBL-2.02";
	public static final String CUSTOMIZATION_BIS3_IR_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:invoice_response:3";
	public static final String CUSTOMIZATION_BIS3_MLR_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:mlr:3";
	
	public static final String CUSTOMIZATION_BIS3_ORDER_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:order:3";
	public static final String CUSTOMIZATION_BIS3_ORDER_RESPONSE_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:order_response:3";

	public static final String CUSTOMIZATION_BIS3_CATALOGUE_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:catalogue:3";
	public static final String CUSTOMIZATION_BIS3_CATALOGUE_RESPONSE_STARTS_WITH = "urn:fdc:peppol.eu:poacc:trns:catalogue_response:3";
	
	public static final String PROFILE_BIS3_ORDER_ONLY = "urn:fdc:peppol.eu:poacc:bis:order_only:3";
	public static final String PROFILE_BIS3_ORDERING = "urn:fdc:peppol.eu:poacc:bis:ordering:3";
	public static final String PROFILE_BIS3_CATALOGUE_ONLY = "urn:fdc:peppol.eu:poacc:bis:catalogue_only:3";
	public static final String PROFILE_BIS3_CATALOGUE_WITHOUT_RESPONSE = "urn:fdc:peppol.eu:poacc:bis:catalogue_wo_response:3";
}
