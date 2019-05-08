package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.constants.DocumentFormatConst;
import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.util.BundleUtil;

public enum DocumentFormat implements Named {

	UNSUPPORTED("U", "", "", DocumentType.UNSUPPORTED),

	CII("CII", "CrossIndustryInvoice", DocumentFormatConst.NS_CII, DocumentType.INVOICE),

	BIS3_INVOICE("BIS3-IN", "Invoice", DocumentFormatConst.NS_UBL_INVOICE, DocumentType.INVOICE),

	BIS3_CREDITNOTE("BIS3-CN", "CreditNote", DocumentFormatConst.NS_UBL_CREDITNOTE, DocumentType.CREDITNOTE),

	/*
	 * BIS Invoice Response 3.0
	 * 
	 * http://docs.peppol.eu/poacc/upgrade-3/profiles/63-invoiceresponse/
	 */
	
	BIS3_INVOICE_RESPONSE("BIS3-INR", "ApplicationResponse", DocumentFormatConst.NS_UBL_APPLICATION_RESPONSE, DocumentType.INVOICE_RESPONSE),

	/*
	 * BIS Message Level Response 3.0
	 * 
	 * http://docs.peppol.eu/poacc/upgrade-3/profiles/36-mlr/
	 */
	BIS3_MESSAGE_LEVEL_RESPONSE("BIS3-MLR", "ApplicationResponse", DocumentFormatConst.NS_UBL_APPLICATION_RESPONSE, DocumentType.MESSAGE_LEVEL_RESPONSE),

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
		this.documentFormatFamily = defineFormatFamily();
	}

	private DocumentFormatFamily defineFormatFamily() {
		if (code.equals("CII")) {
			return DocumentFormatFamily.CII;
		}
		if (code.equals("BIS3-INR")) {
			return DocumentFormatFamily.BIS3_IR;
		}
		if (code.startsWith("BIS3")) {
			return DocumentFormatFamily.BIS3;
		}
		return DocumentFormatFamily.OIOUBL;
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
	
	public boolean isBIS3IR() {
		return this == BIS3_INVOICE_RESPONSE;
	}
	
	public boolean isBIS3MLR() {
		return this == BIS3_MESSAGE_LEVEL_RESPONSE;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	
	public DocumentFormatFamily getDocumentFormatFamily() {
		return documentFormatFamily;
	}

	@Override
	public String getName() {
		return BundleUtil.getName(this);
	}
}
