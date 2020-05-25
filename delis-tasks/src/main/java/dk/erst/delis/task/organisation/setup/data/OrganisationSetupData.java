package dk.erst.delis.task.organisation.setup.data;

import java.util.HashSet;
import java.util.Set;

import dk.erst.delis.data.entities.organisation.Organisation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrganisationSetupData {

	private Organisation organisation;
	private boolean smpIntegrationPublish;
	private Set<OrganisationSubscriptionProfileGroup> subscribeProfileSet;
	private OrganisationReceivingFormatRule receivingFormatRule;
	private OrganisationReceivingMethod receivingMethod;
	private String receivingMethodSetup;
	private Long as2;
	private Long as4;
	private boolean generateInvoiceResponseOnError;
	private boolean sendUndeliverableInvoiceResponseToERST;
	private boolean sendAllInvoiceResponseToERST;
	private boolean onErrorAutoSendEmailToSupplier;
	private String onErrorSenderEmailAddress;
	private String onErrorReceiverEmailAddress;
	
	private boolean receiveBothOIOUBLBIS3;
	
	private boolean checkDeliveredConsumed;
	private int checkDeliveredAlertMins = 0;
	
	public OrganisationSetupData() {
		this.subscribeProfileSet = new HashSet<OrganisationSubscriptionProfileGroup>();
	}
	
	public boolean validatePublishReady() {
		boolean endpointConfigured = this.as2 != null || this.as4 != null;
		if (endpointConfigured) {
			return this.subscribeProfileSet != null && !this.subscribeProfileSet.isEmpty();
		}
		return false;
	}
}
