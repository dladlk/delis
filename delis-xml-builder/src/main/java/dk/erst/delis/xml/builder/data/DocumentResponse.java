package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentResponse {

	private Response response;
	private DocumentReference documentReference;
	private LineResponse[] lineResponse;
	private Party issuerParty;
	private Party recipientParty;

}
