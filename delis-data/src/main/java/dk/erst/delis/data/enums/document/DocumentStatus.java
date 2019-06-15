package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentStatus implements Named {

	LOAD_OK, LOAD_ERROR, // Loading phase

	UNKNOWN_RECEIVER, // We did not manage to find a receipient for the document
	
	VALIDATE_START, VALIDATE_OK, VALIDATE_ERROR, // Validation phase
	
	EXPORT_START, EXPORT_OK,
	
	DELIVER_OK
	
	;
	
	public boolean isLoadFailed() {
		return this == LOAD_ERROR || this == UNKNOWN_RECEIVER; 
	}
}
