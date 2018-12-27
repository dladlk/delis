package dk.erst.delis.data;

public enum RuleDocumentValidationType {

	XSD,
	
	SCHEMATRON
	
	;
	
	public boolean isXSD() {
		return this == XSD;
	}
}
