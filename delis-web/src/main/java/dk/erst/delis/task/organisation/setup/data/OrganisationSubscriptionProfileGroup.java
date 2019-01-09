package dk.erst.delis.task.organisation.setup.data;

public enum OrganisationSubscriptionProfileGroup {

	CII("urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::D16B"),
	
	BIS3(
			
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1", 
	
			"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"
	),
	
	OIOUBL(
			
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0",
			
			"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##OIOUBL-2.02::2.0"
	
	);
	
	private final String[] documentIdentifiers;
	
	private OrganisationSubscriptionProfileGroup(String ... documentIdentifiers) {
		this.documentIdentifiers = documentIdentifiers;
	}

	public String[] getDocumentIdentifiers() {
		return documentIdentifiers;
	}
	
	public String getCode() {
		return this.name();
	}
	
	public String getProcessScheme() {
		return "cenbii-procid-ubl";
	}
	
	public String getProcessId() {
		return "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";
	}
}
