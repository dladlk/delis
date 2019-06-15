package dk.erst.delis.domibus.sender.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

import dk.erst.delis.document.sbdh.SBDHTranslator;
import dk.erst.delis.domibus.sender.ConfigProperties;

public class SendServiceTest {

	private static final String TEST_FOLDER = "../delis-resources/examples/xml/";
	private static final String TEST_FILE = TEST_FOLDER + "BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml";
	private boolean doMinikubeTest = false;

	private ConfigProperties cp = new ConfigProperties() {
		{
			this.setWsDumpHttp(true);
			if (doMinikubeTest) {
				cp.setWsLogin("admin");
				cp.setWsPassword("123456");
				cp.setWsdlUrl("http://dt1.default.svc.cluster.local:8080/services/backend?wsdl");
				cp.setWsForceHttps(false);
			}
		}
	};
	private SendService s = new SendService(cp);

	@Test
	public void testSendNoSBDH() throws Exception {
		assertTrue(s.send(new File(TEST_FILE)).isSuccess());
	}

	@Test
	public void testSendSBDH() throws Exception {
		File tempSourceFile = File.createTempFile("SendServiceTest_testSendSBDH_", "tmp.xml");
		SBDHTranslator service = new SBDHTranslator();
		service.addHeader(Paths.get(TEST_FILE), tempSourceFile.toPath());
		assertTrue(s.send(tempSourceFile).isSuccess());
	}

}
