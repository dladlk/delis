package dk.erst.delis.xml.builder.data;

import lombok.Data;

@Data
public class InvoiceResponseData {

	private String id;
	private String issueDate;
	private String issueTime;
	private String note;
	private Party senderParty;
	private Party receiverParty;
	private DocumentResponse documentResponse;

}
