package dk.erst.delis.web.organisation;

import dk.erst.delis.data.Organisation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrganisationData {

	private Organisation organisation;
	private int identifierCount;
	private int documentCount;
}
