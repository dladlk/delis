package dk.erst.delis.domibus.sender.service;

import java.io.File;

import org.junit.Test;

import dk.erst.delis.domibus.sender.ConfigProperties;

public class SendServiceTest {

	@Test
	public void testSend() throws Exception {
		
		com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump = true;
		
		ConfigProperties cp = new ConfigProperties();
		SendService s = new SendService(cp);
		
		//cp.setWsLogin("domibus_admin");
		//cp.setWsPassword("Systest1_");
		
		//cp.setWsdlUrl("https://edelivery-test.trueservice.dk/domibus1/services/backend?wsdl");
		
		s.send(new File("/wsh/delis/delis-resources/examples/xml/BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml"));
	}

}
