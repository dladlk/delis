package dk.erst.delis.data;

public enum DocumentStatus {

	LOAD_OK, LOAD_ERROR, 

	UNKNOWN_RECEIVER,
	
	VALIDATE_START, VALIDATE_OK, VALIDATE_ERROR, 
	
	EXPORT_START, EXPORT_OK, 
	
	DELIVER_OK
	
	;
	
	public boolean isLoadFailed() {
		return this == LOAD_ERROR || this == UNKNOWN_RECEIVER; 
	}
}
