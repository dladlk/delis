package dk.erst.delis.task.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dk.erst.delis.data.DocumentFormat;

public enum TestDocument {

	CII("CII_invoice_example.xml", DocumentFormat.CII),

	BIS3_INVOICE("BIS3_Invoice_Example_DK_Supplier_Master.xml", DocumentFormat.BIS3_INVOICE),

	BIS3_CREDITNOTE("BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml", DocumentFormat.BIS3_CREDITNOTE),

	OIOUBL_INVOICE("OIOUBL_Invoice_v2p2.xml", DocumentFormat.OIOUBL_INVOICE),

	OIOUBL_CREDITNOTE("OIOUBL_CreditNote_v2p2.xml", DocumentFormat.OIOUBL_CREDITNOTE),

	;

	private static String TEST_EXAMPLE_ROOT_PATH = "../delis-resources/examples/xml/";

	private final String filename;
	private final DocumentFormat documentFormat;

	private TestDocument(String filename, DocumentFormat documentFormat) {
		this.filename = filename;
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
}
