package dk.erst.delis.data.enums.rule;

public enum RuleDocumentValidationType {

	XSD,
	
	SCHEMATRON
	
	;
	
	public boolean isXSD() {
		return this == XSD;
	}
}
