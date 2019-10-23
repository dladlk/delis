package dk.erst.delis.web.document.ir;

import dk.erst.delis.task.email.EmailData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailResponseForm implements EmailData {

	private long documentId;
	
	private String subject;
	private String from;
	private String to;
	
	private String body;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("documentId #");
		sb.append(this.documentId);
		sb.append(" to=");
		sb.append(getTo());
		sb.append(" '");
		sb.append(getSubject());
		sb.append("'");
		return sb.toString();
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}	
}
