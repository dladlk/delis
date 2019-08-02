package dk.erst.delis.web.document.ir;

public class MessageLevelResponseConst {

    public static String[][] useCaseList = new String[][] {

            new String[] { "", "Select use case" },

            new String[] { "1", "Positive response" },

            new String[] { "2", "Negative response – violation of business rules" },

            new String[] { "3", "Negative response – violation of syntax and business rules" },

    };

    public static String[][] applicationResponseTypeCodeList = new String[][] {

            new String[] { "AB", "Message acknowledgement ", "Indicates that an acknowledgement relating to receipt of message or transaction is required. Status is used when Buyer has received a readable invoice message that can be understood and submitted for processing by the Buyer. " },

            new String[] { "AP", "Accepted", "Indication that the referenced offer or transaction (e.g., cargo booking or quotation request) has been accepted. Status is used only when the Buyer has given a final approval of the invoice and the next step is payment " },

            new String[] { "RE", "Rejected", "Indication that the referenced offer or transaction (e.g., cargo booking or quotation request) is not accepted. Status is used only when the Buyer will not process the referenced Invoice any further. Buyer is rejecting this invoice but not necessarily the commercial transaction. Although it can be used also for rejection for commercial reasons (invoice not corresponding to delivery). " },

    };

    public static String[][] applicationResponseLineResponseCodeList = new String[][] {applicationResponseTypeCodeList[2], applicationResponseTypeCodeList[1]};

    public static String[][] applicationResponseLineReasonCodeList = new String[][] {

            new String[] { "SV", "Syntax validation"},

            new String[] { "BV", "Business rule validation, fatal"},

            new String[] { "BW", "Business rule validation, warning"},

    };
}
