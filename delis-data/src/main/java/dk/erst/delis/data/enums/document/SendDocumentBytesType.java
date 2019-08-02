package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum SendDocumentBytesType implements Named {

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
