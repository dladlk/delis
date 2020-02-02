package dk.erst.delis.domibus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import dk.erst.delis.domibus.util.PmodeXmlTemplateConfig.SpringTemplateEngineWrapper;
import dk.erst.delis.domibus.util.pmode.PmodeData;
import dk.erst.delis.domibus.util.pmode.PmodeUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmodeXmlTemplateConfig.class)

public class PmodeXmlServiceTest {

	@Autowired
	private SpringTemplateEngineWrapper pmodeXmlTemplateEngineWrapper;

	@Test
	public void testPmode() throws Exception {
		PmodeXmlService s = new PmodeXmlService(pmodeXmlTemplateEngineWrapper);

		PmodeData pmode = new PmodeData();
		String endpointUrl = "http://localhost:8080";
		pmode.setEndpointUrl(endpointUrl + "/services/msh");
		pmode.setPartyName("domibus_gw1");
		pmode = PmodeUtil.populateServicesActionsLegs(pmode);
		String xml = s.build(pmode);

		System.out.println(xml);
		assertNotNull(xml);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new InputSource(new java.io.StringReader(xml)));

		NodeList service = doc.getElementsByTagName("service");
		assertEquals(15, service.getLength());
		NodeList action = doc.getElementsByTagName("action");
		assertEquals(17, action.getLength());
		NodeList legConfiguration = doc.getElementsByTagName("legConfiguration");
		assertEquals(45, legConfiguration.getLength());
		NodeList legs = doc.getElementsByTagName("leg");
		assertEquals(180, legs.getLength());
	}

}
