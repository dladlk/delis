package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ResponseStatus {

	private String statusReasonCode;
	private String statusReason;
	private String conditionAttributeID;
	private String conditionDescription;

}
