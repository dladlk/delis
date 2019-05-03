package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class DocumentReference {

	private String id;
	private String issueDate;
	private String documentTypeCode;
	private String documentTypeCodeListId;
	private String versionId;
	
}
