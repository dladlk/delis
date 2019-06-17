package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

public enum DocumentProcessStepType implements Named {

	VALIDATE_XSD,
	
	VALIDATE_SCH,
	
	TRANSFORM,
	
	RESOLVE_TYPE, 
	
	COPY,
	
	LOAD,
	
	DELIVER,

	MANUAL,
	
	GENERATE_RESPONSE,
	
	;
	
	public boolean isXsd() {
		return this == VALIDATE_XSD;
	}
	
	public boolean isValidation() {
		return this == VALIDATE_SCH || this == VALIDATE_XSD;
	}
}
