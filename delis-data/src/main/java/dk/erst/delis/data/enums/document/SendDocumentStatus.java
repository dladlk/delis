package dk.erst.delis.data.enums.document;

public enum SendDocumentStatus {

	NEW,

	VALIDATE_START, VALID, VALIDATE_ERROR,

	SEND_START, SEND_OK, SEND_ERROR,

	DELIVERED,

	;

}
