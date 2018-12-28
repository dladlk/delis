package dk.erst.delis.task.document.process.log;

public enum DocumentProcessStepType {

	VALIDATE_XSD,
	
	VALIDATE_SCH,
	
	TRANSFORM,
	
	RESOLVE_TYPE, 
	
	COPY,
	
	LOAD,
	
	DELIVER
}
