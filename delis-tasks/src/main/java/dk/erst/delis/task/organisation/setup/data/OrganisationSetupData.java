package dk.erst.delis.task.organisation.setup.data;

import dk.erst.delis.data.entities.organisation.Organisation;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrganisationSetupData {

	private Organisation organisation;
	private Set<OrganisationSubscriptionProfileGroup> subscribeProfileSet;
	private OrganisationReceivingFormatRule receivingFormatRule;
	private OrganisationReceivingMethod receivingMethod;
	private String receivingMethodSetup;
	private Long as2;
	private Long as4;
	private boolean generateInvoiceResponseOnError;
	private boolean sendUndeliverableInvoiceResponseToERST;
	private boolean sendAllInvoiceResponseToERST;
	
	private boolean receiveBothOIOUBLBIS3;
}
