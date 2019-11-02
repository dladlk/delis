package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentErrorCode implements Named {

	CII_XSD,
	
	CII_SCH,
	
	BIS3_XSD,
	
	BIS3_SCH,
	
	OIOUBL_XSD,
	
	OIOUBL_SCH,
	
	OTHER,
	
	;
	
	public boolean isXSD() {
		return this.name().endsWith("_XSD");
	}

	public boolean isSCH() {
		return this.name().endsWith("_SCH");
	}
	
}
