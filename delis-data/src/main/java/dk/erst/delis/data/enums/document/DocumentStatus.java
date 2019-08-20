package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentStatus implements Named {

	LOAD_OK, LOAD_ERROR(true), // Loading phase

	UNKNOWN_RECEIVER(true), // We did not manage to find a receipient for the document
	
	VALIDATE_START, VALIDATE_OK, VALIDATE_ERROR(true), // Validation phase
	
	EXPORT_START, EXPORT_OK,
	
	DELIVER_OK
	
	;
	
	private boolean error;

	private DocumentStatus() {
		this.error = false;
	}

	private DocumentStatus(boolean error) {
		this.error = error;
	}
	
	public boolean isLoadFailed() {
		return this == LOAD_ERROR || this == UNKNOWN_RECEIVER; 
	}

	public boolean isError() {
		return this.error;
	}
}
