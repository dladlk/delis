package dk.erst.delis.xml.builder.data;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentResponse {

	private Response response;
	private DocumentReference documentReference;
	private List<LineResponse> lineResponse;
	private Party issuerParty;
	private Party recipientParty;

}
