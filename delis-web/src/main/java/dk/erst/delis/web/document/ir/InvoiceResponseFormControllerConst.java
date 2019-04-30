package dk.erst.delis.web.document.ir;

public class InvoiceResponseFormControllerConst {

	public static String[][] useCaseList = new String[][] {

			new String[] { "", "Select use case" },

			new String[] { "1", "Invoice in process" },

			new String[] { "2a", "Additional reference data", "Use case shows an invoice that is in process and states the date when the invoice was received and entered into processing which in this case is shown as a day before the IMR is sent. \r\n" + "The Buyer also communicates that his internal reference number is X001." },

			new String[] { "2b", "In process but postponed", "Invoice is in processing but the processing is on hold until future date. This is communicated by showing an as status effective date in the future." },

			new String[] { "3", "Invoice is accepted" },

			new String[] { "4a", "Invoice is rejected" },

			new String[] { "4b", "Rejected requesting reissue" },

			new String[] { "4c", "Rejected requesting replacement" },

			new String[] { "5", "Invoice is conditionally accepted", "The buyer sets a new due date as 2018-01-15 using the Business term identifier BT-9 for Due Date as defined in EN 16931." },

			new String[] { "6a", "Under query missing information", "The use case assumes missing purchase order reference and proposes that the reference should be PO0001, the invoice is put under query 2 days after it was issued." },

			new String[] { "6b", "Missing PO", "The use case assumes missing purchase order reference and proposes that the reference should be PO0001, the invoice is put under query 2 days after it was issued." },

			new String[] { "6c", "Wrong detail partial credit", "Use case demonstrates how line level corrections are given with textual notes." },

			new String[] { "7", "Payment has been initiated", "Use case shows where payment has been initated on 2017-12-30." },

			new String[] { "8", "Invoice is accepted by third party", "Use case shows where Invoice processing service sends an acceptance to the Seller company for an invoice that was issued to Buyer A." } };

	public static String[][] invoiceStatusCodeList = new String[][] {

			new String[] { "AB", "Message acknowledgement ", "Indicates that an acknowledgement relating to receipt of message or transaction is required. Status is used when Buyer has received a readable invoice message that can be understood and submitted for processing by the Buyer. " },

			new String[] { "AP", "Accepted ", "Indication that the referenced offer or transaction (e.g., cargo booking or quotation request) has been accepted. Status is used only when the Buyer has given a final approval of the invoice and the next step is payment " },

			new String[] { "RE", "Rejected ", "Indication that the referenced offer or transaction (e.g., cargo booking or quotation request) is not accepted. Status is used only when the Buyer will not process the referenced Invoice any further. Buyer is rejecting this invoice but not necessarily the commercial transaction. Although it can be used also for rejection for commercial reasons (invoice not corresponding to delivery). " },

			new String[] { "IP", "In process ", "Indicates that the referenced message or transaction is being processed. Status is used when the processing of the Invoice has started in Buyers system. " },

			new String[] { "UQ", "Under query ", "Indicates that the processing of the referenced message has been halted pending response to a query. Status is used when Buyer will not proceed to accept the Invoice without receiving additional information from the Seller." },

			new String[] { "CA", "Conditionally accepted ", "Indication that the referenced offer or transaction (e.g., cargo booking or quotation request) has been accepted under conditions indicated in this message. Status is used when Buyer is accepting the Invoice under conditions stated in ‘Status Reason’ and proceed to pay accordingly unless disputed by Seller." },

			new String[] { "PD", "Paid ", "Indicates that the referenced document or transaction has been paid. Status is used only when the Buyer has initiated the payment of the invoice." } };

	public static String[][] statusActionList = new String[][] {

			new String[] { "NOA", "No action required ", "No action required" },

			new String[] { "PIN", "Provide information ", "Missing information requested without re-issuing invoice" },

			new String[] { "NIN", "Issue new invoice ", "Request to re-issue a corrected invoice" },

			new String[] { "CNF", "Credit fully ", "Request to fully cancel the referenced invoice with a credit note" },

			new String[] { "CNP", "Credit partially ", "Request to issue partial credit note for corrections only" },

			new String[] { "CNA", "Credit the amount ", "Request to repay the amount paid on the invoice" },

			new String[] { "OTH", "Other ", "Requested action is not defined by code" }

	};

	public static String[][] statusReasonList = new String[][] {

			new String[] { "NON", "No Issue ", "Indicates that receiver of the documents sends the message just to update the status and there are no problems with document processing" },

			new String[] { "REF", "References incorrect ", "Indicates that the received document did not contain references as required by the receiver for correctly routing the document for approval or processing." },

			new String[] { "LEG", "Legal information incorrect ", "Information in the received document is not according to legal requirements." },

			new String[] { "REC", "Receiver unknown ", "The party to which the document is addressed is not known." },

			new String[] { "QUA", "Item quality insufficient ", "Unacceptable or incorrect quality" },

			new String[] { "DEL", "Delivery issues ", "Delivery proposed or provided is not acceptable." },

			new String[] { "PRI", "Prices incorrect ", "Prices not according to previous expectation." },

			new String[] { "QTY", "Quantity incorrect ", "Quantity not according to previous expectation." },

			new String[] { "ITM", "Items incorrect ", "Items not according to previous expectation." },

			new String[] { "PAY", "Payment terms incorrect ", "Payment terms not according to previous expectation." },

			new String[] { "UNR", "Not recognized ", "Commercial transaction not recognized." },

			new String[] { "FIN", "Finance incorrect ", "Finance terms not according to previous expectation." },

			new String[] { "OTH", "Other", "Reason for status is not defined by code." } };

}
