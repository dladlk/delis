package dk.erst.delis.data.enums.document;

public enum SendDocumentBytesType {

	ORIGINAL("Original format"), 
	
	OUT_SBD("Outgoing SBD envelope"), 
	
	RECEIPT("Outgoing receipt"),
	
	;

	private final String technicalDescription;
	
	SendDocumentBytesType(String desc) {
		technicalDescription = desc;
	}

	public String getTechnicalDescription() {
		return technicalDescription;
	}
	
	public String getCode() {
		return this.name();
	}
	
	public boolean isReceipt() {
		return this == RECEIPT;
	}

}
