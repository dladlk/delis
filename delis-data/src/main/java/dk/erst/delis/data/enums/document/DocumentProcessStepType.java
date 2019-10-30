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
	
	CHECK_DELIVERY,
	
	SEND_ERROR_EMAIL,
	;
	
	public boolean isXsd() {
		return this == VALIDATE_XSD;
	}
	
	public boolean isValidation() {
		return this == VALIDATE_SCH || this == VALIDATE_XSD;
	}
}
