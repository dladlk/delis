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

	;

	public boolean isResponse() {
		return this == INVOICE_RESPONSE || this == MESSAGE_LEVEL_RESPONSE;
	}

	public String getCode() {
		return name();
	}

}
