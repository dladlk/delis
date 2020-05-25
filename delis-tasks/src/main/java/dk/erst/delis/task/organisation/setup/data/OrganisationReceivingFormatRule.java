package dk.erst.delis.task.organisation.setup.data;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;

public enum OrganisationReceivingFormatRule {

	OIOUBL("OIOUBL - convert invoices and credit notes to OIOUBL"),
	
	BIS3("Prefer BIS3 - convert CII to BIS3, but keep BIS3 and OIOUBL"),
	
	BIS3_POSITIVE("[Not implemented]: BIS3 positive - like BIS3 but keep BIS3 only if it is positive amount, otherwise convert to proper BIS3"),
	
	KEEP_ORIGINAL("No transformation - keep ingoing to C3")
	
	;
	
	private final String title;

	private OrganisationReceivingFormatRule(String title) {
		this.title = title;
	}
	
	public String getCode() {
		return name();
	}

	public String getTitle() {
		return title;
	}

	public static OrganisationReceivingFormatRule getDefault() {
		return OrganisationReceivingFormatRule.OIOUBL;
	}

	public boolean isLast(DocumentFormat documentFormat) {
		if (documentFormat == null || documentFormat == DocumentFormat.UNSUPPORTED) {
			return true;
		}
		if (!documentFormat.getDocumentType().isInvoiceOrCreditNote()) {
			return true;
		}
		DocumentFormatFamily documentFormatFamily = documentFormat.getDocumentFormatFamily();
		if (documentFormatFamily == null || documentFormatFamily == DocumentFormatFamily.UNSUPPORTED) {
			return true;
		}
		if (documentFormatFamily == DocumentFormatFamily.BIS3_IR) {
			return true;
		}
		switch (this) {
		case KEEP_ORIGINAL:
			return true;
		case BIS3:
		case BIS3_POSITIVE:
			return documentFormatFamily == DocumentFormatFamily.BIS3 || documentFormatFamily == DocumentFormatFamily.OIOUBL;
		case OIOUBL:
			if (documentFormatFamily == DocumentFormatFamily.OIOUBL) {
				return true;
			}
			break;
		}
		return false;
	}
}
