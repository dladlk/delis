package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Response {

	private String responseCode;
	private String effectiveDate;
	private ResponseStatus status;
	
}
