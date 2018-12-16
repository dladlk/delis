package dk.erst.delis.data;

public enum DocumentFormatFamily {

	UNSUPPORTED, CII, BIS3, OIOUBL;
	
	public boolean isLast() {
		return this == OIOUBL;
	}
}
