package dk.erst.delis.domibus.util.pmode;


/*
 * Copied from https://www.galaxygw.com/peppol-documents/ 
 * 
 * which references https://github.com/OpenPEPPOL/documentation/tree/master/TransportInfrastructure
 */
public enum PModeProfileGroup {

	/*
	 * PEPPOL EHF3 Ordering
	 * 
	 * https://anskaffelser.dev/postaward/g3/spec/current/ordering-3.0/
	 */
	
	EHF3_Ordering("urn:fdc:anskaffelser.no:2019:ehf:postaward:g3:02:1.0",
			
			new String[] {
					
					"urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.1", 
					
					"urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3:extended:urn:fdc:anskaffelser.no:2019:ehf:spec:3.0::2.1"
					
	}),

//	CII("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0",
//			
//			new String[] {
//					
//					"urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::D16B"
//			}),
//	
	/*
	 * PEPPOL BIS Billing - Post-Award Coordinating Community version 3.0.2
	 * 
	 * http://docs.peppol.eu/poacc/billing/3.0/bis/
	 */
	
	// Document identifiers should be grouped under the same process id. That is why CrossIndustryInvoice is here
	CII_BIS3("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0",
			
			new String[] {
			
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1", 
	
			"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1",
			
			"urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::D16B"
	
			}),
	
	OIOUBL("urn:www.nesubl.eu:profiles:profile5:ver2.0", 
			
			new String[] {
					
			"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0",
			
			"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##OIOUBL-2.02::2.0"
			
			}),
	
	/*
	 * BIS Invoice Response 3.0
	 * 
	 * http://docs.peppol.eu/poacc/upgrade-3/profiles/63-invoiceresponse/
	 */
	
	BisInvoiceResponse30("urn:fdc:peppol.eu:poacc:bis:invoice_response:3", 
			
			new String[] {
					
			"urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:invoice_response:3::2.1"
					
			}),	
	
	/*
	 * BIS Invoice Response 3.0
	 * 
	 * http://docs.peppol.eu/poacc/upgrade-3/profiles/36-mlr/
	 */
	
	MessageLevelResponse30("urn:fdc:peppol.eu:poacc:bis:mlr:3", 
			
			new String[] {
					
			"urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:mlr:3::2.1"
					
			}),		
	
	
	/*
	 * BIS Order only 3.1
	 * 
	 * https://docs.peppol.eu/poacc/upgrade-3/profiles/3-order-only/
	 */
	
	BIS3_OrderOnly ("urn:fdc:peppol.eu:poacc:bis:order_only:3", 
			
			new String[] {
					
			"urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3::2.1"
			
			}),	
	
	/*
	 * BIS Ordering 3.1
	 * 
	 * https://docs.peppol.eu/poacc/upgrade-3/profiles/28-ordering/
	 */
	
	BIS3_Ordering ("urn:fdc:peppol.eu:poacc:bis:ordering:3", 
			
			new String[] {
					
					"urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:fdc:peppol.eu:poacc:trns:order:3::2.1",
					
					"urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:fdc:peppol.eu:poacc:trns:order_response:3::2.1"
					
	}),	
	
	/*
	 * PEPPOL BIS Catalogue Only 3.0
	 * 
	 * https://docs.peppol.eu/poacc/upgrade-3/profiles/1-catalogueonly/
	 */
	
	BIS3_Catalogue_Only("urn:fdc:peppol.eu:poacc:bis:catalogue_only:3", 
			
			new String[] {
					
					"urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:fdc:peppol.eu:poacc:trns:catalogue:3::2.1",
					
					"urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:fdc:peppol.eu:poacc:trns:catalogue_response:3::2.1"
					
	}),	
	;
	
	public static String DEFAULT_PROCESS_SCHEME_ID = "urn:fdc:peppol.eu:2017:identifiers:proc-id";
	
	private final String processId;
	private final String[] documentIdentifiers;
	
	private PModeProfileGroup(String processId, String[] documentIdentifiers) {
		this.processId = processId;
		this.documentIdentifiers = documentIdentifiers;
	}

	public String[] getDocumentIdentifiers() {
		return documentIdentifiers;
	}
	
	public String getCode() {
		return this.name();
	}
	
	public String getProcessType() {
		return DEFAULT_PROCESS_SCHEME_ID;
	}
	
	public String getProcessSchemeSMP() {
		if (this == OIOUBL) {
			return "nes-procid-ubl";
		}
		return "cenbii-procid-ubl";
	}
	
	public String getProcessId() {
		return this.processId;
	}
}
