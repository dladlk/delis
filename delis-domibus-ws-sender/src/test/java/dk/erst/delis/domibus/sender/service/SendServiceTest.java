package dk.erst.delis.domibus.sender.service;

import java.io.File;

import org.junit.Test;

import dk.erst.delis.domibus.sender.ConfigProperties;

public class SendServiceTest {

	private boolean doMinikubeTest = false;

	@Test
	public void testSend() throws Exception {
		ConfigProperties cp = new ConfigProperties();
		SendService s = new SendService(cp);

		cp.setWsDumpHttp(true);
		
		if (doMinikubeTest) {
			cp.setWsLogin("admin");
			cp.setWsPassword("123456");
			cp.setWsdlUrl("http://dt1.default.svc.cluster.local:8080/services/backend?wsdl");
			cp.setWsForceHttps(false);
		}
		s.send(new File("../delis-resources/examples/xml/BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml"));
	}

}
