package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.constants.DocumentFormatConst;

public enum DocumentFormat {

	UNSUPPORTED("U", "", "", DocumentType.UNSUPPORTED),

	CII("CII", "CrossIndustryInvoice", DocumentFormatConst.NS_CII, DocumentType.INVOICE),

	BIS3_INVOICE("BIS3-IN", "Invoice", DocumentFormatConst.NS_UBL_INVOICE, DocumentType.INVOICE),

	BIS3_CREDITNOTE("BIS3-CN", "CreditNote", DocumentFormatConst.NS_UBL_CREDITNOTE, DocumentType.CREDITNOTE),

	OIOUBL_INVOICE("OIO-IN", "Invoice", DocumentFormatConst.NS_UBL_INVOICE, DocumentType.INVOICE),

	OIOUBL_CREDITNOTE("OIO-CN", "CreditNote", DocumentFormatConst.NS_UBL_CREDITNOTE, DocumentType.CREDITNOTE),

	;

	private final String code;
	private final String namespace;
	private final String rootTag;
	private final DocumentType documentType;
	private final DocumentFormatFamily documentFormatFamily;

	private DocumentFormat(String code, String rootTag, String namespace, DocumentType documentType) {
		this.code = code;
		this.rootTag = rootTag;
		this.namespace = namespace;
		this.documentType = documentType;
		this.documentFormatFamily = (code.equals("CII") ? DocumentFormatFamily.CII : (code.startsWith("BIS3") ? DocumentFormatFamily.BIS3 : DocumentFormatFamily.OIOUBL));
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

	public DocumentType getDocumentType() {
		return documentType;
	}
	
	public DocumentFormatFamily getDocumentFormatFamily() {
		return documentFormatFamily;
	}


}
