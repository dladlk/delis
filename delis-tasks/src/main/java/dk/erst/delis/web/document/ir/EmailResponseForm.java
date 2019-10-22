package dk.erst.delis.web.document.ir;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailResponseForm {

	private long documentId;
	
	private String subject;
	private String from;
	private String to;
	
	private String body;
	
}
