package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentResponse {

	private Response response;
	private DocumentReference documentReference;
	private Party issuerParty;
	
}
