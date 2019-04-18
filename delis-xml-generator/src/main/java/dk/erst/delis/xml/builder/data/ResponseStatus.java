package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseStatus {

	private String statusReasonCode;
	private String statusReason;
	private String conditionAttributeID;
	private String conditionDescription;

}
