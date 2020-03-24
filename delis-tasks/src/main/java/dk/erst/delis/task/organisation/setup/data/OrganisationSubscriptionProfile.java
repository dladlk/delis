package dk.erst.delis.task.organisation.setup.data;

public enum OrganisationSubscriptionProfile {

	CII_INVOICE("UN/CEFACT CII Invoice", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::D16B"),
	
	PEPPOL_BIS3_INVOICE("PEPPOL BIS3 Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),

	PEPPOL_BIS3_CREDITNOTE("PEPPOL BIS3 CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),

	OIOUBL_NES5_INVOICE("OIOUBL NES5 Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0"), 

	OIOUBL_NES5_CREDITNOTE("OIOUBL NES5 CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##OIOUBL-2.02::2.0"), 

	PEPPOL_BIS3_INVOICE_RESPONSE("PEPPOL BIS IR 3.0", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:invoice_response:3::2.1"), 

	PEPPOL_BIS3_MESSAGE_LEVEL_RESPONSE("PEPPOL BIS MLR 3.0", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:mlr:3::2.1"),
	
	PEPPOL_BIS3_ORDER_ONLY("PEPPOL BIS3 Order Only", "urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:bis:order_only:3::2.1"),

	PEPPOL_BIS3_ORDER("PEPPOL BIS3 Order", "urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:bis:ordering:3::2.1"),

	PEPPOL_BIS3_ORDER_RESPONSE("PEPPOL BIS3 Order Response", "urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:bis:ordering:3::2.1"),

	PEPPOL_BIS3_CATALOGUE("PEPPOL BIS3 Catalogue Only", "urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:fdc:peppol.eu:poacc:bis:catalogue_only:3::2.1"),

	PEPPOL_BIS3_CATALOGUE_RESPONSE("PEPPOL BIS3 Catalogue Only Response", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:bis:catalogue_only:3::2.1"),
	;
	
	private final String name;
	private final String documentIdentifier;

	private OrganisationSubscriptionProfile(String name, String documentIdentifier) {
		this.name = name;
		this.documentIdentifier = documentIdentifier;
	}

	public String getDocumentIdentifier() {
		return documentIdentifier;
	}

	public String getName() {
		return name;
	}
	
}
