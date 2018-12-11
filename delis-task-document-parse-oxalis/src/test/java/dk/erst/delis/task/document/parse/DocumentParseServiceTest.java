package dk.erst.delis.task.document.parse;

import java.io.FileInputStream;

import org.junit.Test;

import no.difi.vefa.peppol.common.model.Header;

public class DocumentParseServiceTest {

	@Test
	public void testParsePeppolHeader() throws Exception {
		DocumentParseService dps = new DocumentParseService();
		Header header = dps.parsePeppolHeader(new FileInputStream("/work/2018.11.27_xml/examples/BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml"));
		System.out.println(header);
	}

}
