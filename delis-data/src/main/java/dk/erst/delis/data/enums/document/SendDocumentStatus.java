package dk.erst.delis.data.enums.document;

public enum SendDocumentStatus {

	NEW,

	VALID, // Schema/schematron valid document
	
	SEND_START, SEND_OK, SEND_ERROR,
	
	DELIVERED
	;
	
}
