package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentBytesType implements Named {

	IN_AS4("Ingoing Domibus AS4 metadata.xml"), 
	
	IN_AS2("Ingoing Oxalis AS2 receipt"), 
	
	IN_SBD("Ingoing original SBD envelope"), 
	
	IN("Ingoing payload"),
	
	INTERM("Intermediate format as result of conversion"),

	READY("Validated and converted, ready for deliver"),

	;

	private final String technicalDescription;
	
	DocumentBytesType(String desc) {
		technicalDescription = desc;
	}

	public String getTechnicalDescription() {
		return technicalDescription;
	}
	
	public String getCode() {
		return this.name();
	}

}
