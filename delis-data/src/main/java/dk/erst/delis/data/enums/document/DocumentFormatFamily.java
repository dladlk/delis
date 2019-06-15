package dk.erst.delis.data.enums.document;

public enum DocumentFormatFamily {

	UNSUPPORTED, CII, BIS3, OIOUBL, BIS3_IR;
	
	public String getCode() {
		return this.name();
	}
	
}
