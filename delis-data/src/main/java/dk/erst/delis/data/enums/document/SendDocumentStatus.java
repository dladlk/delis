package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

/*
 * Max length of name 25
 */
public enum SendDocumentStatus implements Named {

	NEW,

	VALIDATE_START, VALID, VALIDATE_ERROR(true),

	SEND_START, SEND_OK, SEND_ERROR(true),

	FORWARD_START, FORWARD_OK, FORWARD_SKIPPED, FORWARD_FAILED(true),
	
	;

	private boolean error;
	
	private SendDocumentStatus() {
	}
	
	private SendDocumentStatus(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return this.error;
	}
	
}
