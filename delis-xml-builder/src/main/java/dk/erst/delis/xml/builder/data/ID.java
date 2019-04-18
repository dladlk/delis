package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ID {

	private String schemeID;
	private String value;
	
}
