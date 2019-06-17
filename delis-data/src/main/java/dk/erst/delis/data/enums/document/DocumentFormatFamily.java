package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentFormatFamily implements Named {

	UNSUPPORTED, CII, BIS3, OIOUBL, BIS3_IR;
	
	public String getCode() {
		return this.name();
	}
	
}
