package dk.erst.delis.task.organisation.setup.data;

import java.util.Set;

import dk.erst.delis.data.AccessPoint;
import dk.erst.delis.data.Organisation;
import lombok.Data;

@Data
public class OrganisationSetupData {

	private Organisation organisation;
	private Set<OrganisationSubscriptionProfileGroup> subscribeProfileSet;
	private OrganisationReceivingFormatRule receivingFormatRule;
	private OrganisationReceivingMethod receivingMethod;
	private String receivingMethodSetup;
	private AccessPoint as2;
	private AccessPoint as4;
	
}
