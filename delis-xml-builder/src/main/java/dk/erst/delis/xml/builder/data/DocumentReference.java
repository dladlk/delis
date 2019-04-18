package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentReference {

	private String id;
	private String issueDate;
	private String documentTypeCode;
	
}
