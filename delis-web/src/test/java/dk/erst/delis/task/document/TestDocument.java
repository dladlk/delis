package dk.erst.delis.task.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dk.erst.delis.data.enums.document.DocumentFormat;

		public enum TestDocument {

	CII("CII_invoice_example.xml", DocumentFormat.CII),

	BIS3_INVOICE("BIS3_Invoice_Example_DK_Supplier_Master.xml", DocumentFormat.BIS3_INVOICE),

	BIS3_CREDITNOTE("BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml", DocumentFormat.BIS3_CREDITNOTE),

	BIS3_INVOICE_RESPONSE("BIS3_InvoiceResponse_Example.xml", DocumentFormat.BIS3_INVOICE_RESPONSE),

	BIS3_MESSAGE_LEVEL_RESPONSE("BIS3_MessageLevelResponse_Example.xml", DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE),

	OIOUBL_INVOICE("OIOUBL_Invoice_v2p2.xml", DocumentFormat.OIOUBL_INVOICE),

	OIOUBL_CREDITNOTE("OIOUBL_CreditNote_v2p2.xml", DocumentFormat.OIOUBL_CREDITNOTE),

	ERROR_XSD_BIS3_INVOICE("error/BIS3_Invoice_XSD.xml ", DocumentFormat.BIS3_INVOICE),

	ERROR_SCH_BIS3_INVOICE("error/BIS3_Invoice_SCH.xml ", DocumentFormat.BIS3_INVOICE),

	ERROR_XSD_OIOUBL_INVOICE("error/OIOUBL_Invoice_XSD.xml ", DocumentFormat.OIOUBL_INVOICE),

	ERROR_SCH_OIOUBL_INVOICE("error/OIOUBL_Invoice_SCH.xml ", DocumentFormat.OIOUBL_INVOICE),

	ERROR_XSD_CII_INVOICE("error/CII_Invoice_XSD.xml ", DocumentFormat.CII),

	ERROR_SCH_CII_INVOICE("error/CII_Invoice_SCH.xml ", DocumentFormat.CII),

			;

	private static String TEST_EXAMPLE_ROOT_PATH = "../delis-resources/examples/xml/";

	private final String filename;
	private final DocumentFormat documentFormat;

	private TestDocument(String filename, DocumentFormat documentFormat) {
		this.filename = filename.trim();
		this.documentFormat = documentFormat;
	}

	public String getFilePath() {
		return TEST_EXAMPLE_ROOT_PATH + this.filename;
	}

	public InputStream getInputStream() {
		try {
			return new FileInputStream(this.getFilePath());
		} catch (FileNotFoundException e) {
			System.err.println("File " + this.getFilePath() + " is not found.");
		}
		return null;
	}

	public DocumentFormat getDocumentFormat() {
		return documentFormat;
	}
	
	public boolean isExpectedSuccess() {
		return !this.name().startsWith("ERROR_");
	}
}
