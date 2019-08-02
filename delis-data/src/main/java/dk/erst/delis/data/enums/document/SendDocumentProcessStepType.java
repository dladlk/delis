package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;

/*
 * Max length of name 25
 */
public enum SendDocumentProcessStepType implements Named {

	CREATE,

	VALIDATE_XSD,

	VALIDATE_SCH,

	SEND,

	MANUAL,

	FORWARD, 
	
	VALIDATE,
	
}
