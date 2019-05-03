package dk.erst.delis.xml.builder.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Contact {

	private String name;
	private String telephone;
	private String electronicMail;
	
}
