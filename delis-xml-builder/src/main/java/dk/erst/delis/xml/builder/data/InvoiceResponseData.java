package dk.erst.delis.xml.builder.data;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvoiceResponseData {

	private String ublVersionID;
	private String profileID = "urn:fdc:peppol.eu:poacc:bis:invoice_response:3";
	private String customizationID = "urn:fdc:peppol.eu:poacc:trns:invoice_response:3";
	private String id;
	private String issueDate;
	private String issueTime;
	private String note;
	private Party senderParty;
	private Party receiverParty;
	private DocumentResponse documentResponse;

}
