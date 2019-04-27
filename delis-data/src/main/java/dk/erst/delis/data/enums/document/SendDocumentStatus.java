package dk.erst.delis.data.enums.document;

/*
 * Max length of name 25
 */
public enum SendDocumentStatus {

	NEW,

	VALIDATE_START, VALID, VALIDATE_ERROR,

	SEND_START, SEND_OK, SEND_ERROR,

	FORWARD_START, FORWARD_OK, FORWARD_SKIPPED, FORWARD_FAILED,
	
	;

}
