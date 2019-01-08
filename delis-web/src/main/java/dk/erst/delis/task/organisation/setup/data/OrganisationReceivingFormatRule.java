package dk.erst.delis.task.organisation.setup.data;

public enum OrganisationReceivingFormatRule {

	OIOUBL("OIOUBL - convert everything to OIOUBL"),
	
	BIS3("BIS3 - CII to BIS3, keep BIS3 and OIOUBL"),
	
	BIS3_POSITIVE("BIS3 positive - like BIS3 but keep BIS3 only if it is positive amount, otherwise convert to proper BIS3"),
	
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
}
