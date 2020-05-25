package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

/*
 * Max length of names - 25
 */
public enum DocumentType implements Named {

	UNSUPPORTED,

	INVOICE,

	CREDITNOTE,

	INVOICE_RESPONSE,

	MESSAGE_LEVEL_RESPONSE,
	
	ORDER,
	
	ORDER_RESPONSE,
	
	CATALOGUE,
	
	CATALOGUE_RESPONSE,

	;

	public boolean isResponse() {
		return this == INVOICE_RESPONSE || this == MESSAGE_LEVEL_RESPONSE;
	}

	public String getCode() {
		return name();
	}

	public boolean isInvoiceOrCreditNote() {
		return this == INVOICE || this == CREDITNOTE;
	}

	public boolean isOrder() {
		return this == ORDER;
	}

	public boolean isOrderResponse() {
		return this == ORDER_RESPONSE;
	}
	
	public boolean isCatalogue() {
		return this == CATALOGUE;
	}
}
