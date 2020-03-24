package dk.erst.delis.domibus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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

		NodeList serviceList = doc.getElementsByTagName("service");
		assertEquals(15 + 3 * 3, serviceList.getLength());
		Set<String> serviceNames = buildNameSet(serviceList);
		assertEquals(serviceList.getLength(), serviceNames.size());

		NodeList actionList = doc.getElementsByTagName("action");
		assertEquals(17 + 5 * 2, actionList.getLength());
		Set<String> actionNames = buildNameSet(actionList);
		assertEquals(actionList.getLength(), actionNames.size());

		NodeList legConfigurationList = doc.getElementsByTagName("legConfiguration");
		assertEquals(45 + (5 * 2 * 3), legConfigurationList.getLength());
		Set<String> legConfigurationNames = buildNameSet(legConfigurationList);
		assertEquals(legConfigurationList.getLength(), legConfigurationNames.size());
		for (int i = 0; i < legConfigurationList.getLength(); i++) {
			NamedNodeMap attributes = legConfigurationList.item(i).getAttributes();
			String legName = attributes.getNamedItem("name").getNodeValue();
			String serviceName = attributes.getNamedItem("service").getNodeValue();
			String actionName = attributes.getNamedItem("action").getNodeValue();

			assertTrue("Not found service " + serviceName + ", defined at leg " + legName, serviceNames.contains(serviceName));
			assertTrue("Not found action " + actionName + ", defined at leg " + legName, actionNames.contains(actionName));
		}

		NodeList legs = doc.getElementsByTagName("leg");
		assertEquals(180 + (5 * 2 * 3 * 4), legs.getLength());
		for (int i = 0; i < legs.getLength(); i++) {
			String legName = legs.item(i).getAttributes().getNamedItem("name").getNodeValue();
			assertTrue("Not found leg name " + legName, legConfigurationNames.contains(legName));
		}
	}

	private Set<String> buildNameSet(NodeList nodeList) {
		Set<String> nameSet = new HashSet<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			nameSet.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
		}
		return nameSet;
	}

}
