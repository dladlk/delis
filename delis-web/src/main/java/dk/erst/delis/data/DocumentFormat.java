package dk.erst.delis.data;

public enum DocumentFormat {

	UNSUPPORTED("U", "", ""),

	CII("CII", "CrossIndustryInvoice", DocumentFormatConst.NS_CII),

	BIS3_INVOICE("BIS3-IN", "Invoice", DocumentFormatConst.NS_UBL_INVOICE),

	BIS3_CREDITNOTE("BIS3-CN", "CreditNote", DocumentFormatConst.NS_UBL_CREDITNOTE),

	OIOUBL_INVOICE("OIO-IN", "Invoice", DocumentFormatConst.NS_UBL_INVOICE),

	OIOUBL_CREDITNOTE("OIO-CN", "CreditNote", DocumentFormatConst.NS_UBL_CREDITNOTE),

	;

	private final String code;
	private final String namespace;
	private final String rootTag;

	private DocumentFormat(String code, String rootTag, String namespace) {
		this.code = code;
		this.rootTag = rootTag;
		this.namespace = namespace;
	}

	public String getCode() {
		return code;
	}

	public boolean isUnsupported() {
		return this == DocumentFormat.UNSUPPORTED;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getRootTag() {
		return rootTag;
	}

	public boolean isOIOUBL() {
		return this == OIOUBL_CREDITNOTE || this == DocumentFormat.OIOUBL_INVOICE;
	}
	
	public boolean isCII() {
		return this == CII;
	}
	
	public boolean isBIS3() {
		return this == BIS3_INVOICE || this == BIS3_CREDITNOTE;
	}


}
