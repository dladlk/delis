package dk.erst.delis.task.document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public enum TestDocument {

	CII("CII_invoice_example.xml"),

	BIS3_INVOICE("BIS3_Invoice_Example_DK_Supplier_Master.xml"),

	BIS3_CREDITNOTE("BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml"),

	OIOUBL_INVOICE("OIOUBL_Invoice_v2p2.xml"),

	OIOUBL_CREDITNOTE("OIOUBL_CreditNote_v2p2.xml"),

	;

	private static String TEST_EXAMPLE_ROOT_PATH = "/work/2018.11.27_xml/examples/";

	private String filename;

	private TestDocument(String filename) {
		this.filename = filename;
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
}
