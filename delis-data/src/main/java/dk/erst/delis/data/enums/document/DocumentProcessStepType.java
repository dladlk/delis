package dk.erst.delis.data.enums.document;

public enum DocumentProcessStepType {

	VALIDATE_XSD,
	
	VALIDATE_SCH,
	
	TRANSFORM,
	
	RESOLVE_TYPE, 
	
	COPY,
	
	LOAD,
	
	DELIVER,

	MANUAL,
	
	GENERATE_RESPONSE,
}
