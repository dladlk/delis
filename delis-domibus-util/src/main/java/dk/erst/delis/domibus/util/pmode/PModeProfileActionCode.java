package dk.erst.delis.domibus.util.pmode;

public enum PModeProfileActionCode {

	ORDER_EHF3("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.1"),
	ORDER_RESPONSE_EHF3("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.1"),
	INVOICE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),
	CREDITNOTE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),
	CROSS_INDUSTRY_INVOICE("urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::D16B"),
	INVOICE_OIOUBL("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0"),
	CREDITNOTE_OIOUBL("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##OIOUBL-2.02::2.0"),
	INVOICE_RESPONSE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:invoice_response:3::2.1"),
	APPLICATION_RESPONSE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:mlr:3::2.1"),
	ORDER_BIS3("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3::2.1"),
	ORDER_RESPONSE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3::2.1"),
	CATALOGUE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:fdc:peppol.eu:poacc:trns:catalogue:3::2.1"),
	CATALOGUE_RESPONSE_BIS3("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:catalogue_response:3::2.1")
	;

	private String documentIdentifier;

	private PModeProfileActionCode(String documentIdentifier) {
		this.documentIdentifier = documentIdentifier;
	}

	public String getDocumentIdentifier() {
		return documentIdentifier;
	}
	
	public static PModeProfileActionCode getInstance(String documentIdentifier) {
		for (PModeProfileActionCode code : PModeProfileActionCode.values()) {
			if (code.getDocumentIdentifier().equals(documentIdentifier)) {
				return code;
			}
		}
		return null;
	}
}
