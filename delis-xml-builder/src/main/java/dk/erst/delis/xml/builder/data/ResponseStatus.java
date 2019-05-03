package dk.erst.delis.xml.builder.data;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseStatus {

	private String statusReasonCode;
	private String statusReasonCodeListId;

	private String statusReason;
	private String conditionAttributeID;
	private String conditionDescription;

	public boolean isFilledCondition() {
		return isNotBlank(conditionAttributeID) || isNotBlank(conditionDescription);
	}

}
