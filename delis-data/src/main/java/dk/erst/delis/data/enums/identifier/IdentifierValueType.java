package dk.erst.delis.data.enums.identifier;

public enum IdentifierValueType {

	GLN("GLN"), DK_CVR("DK:CVR"), OTHER("OTHER");
	
	private String code;

	private IdentifierValueType(String code) {
		this.code = code;
	}

	public static IdentifierValueType getInstance(String code) {
		IdentifierValueType[] values = values();
		for (IdentifierValueType ivt : values) {
			if (ivt.getCode().equalsIgnoreCase(code)) {
				return ivt;
			}
		}
		return OTHER;
	}
	
	public boolean isOther() {
		return this == OTHER;
	}
	
	public String getCode() {
		return code;
	}
}
