package dk.erst.delis.task.document.process.validate.result;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OIOUBLSchematronResultCollector implements ISchematronResultCollector {

	public static boolean DUMP_NODE_VALUE_INSTEAD_OF_MESSAGE = false;

	protected static OIOUBLSchematronResultCollector INSTANCE = new OIOUBLSchematronResultCollector();

	private OIOUBLSchematronResultCollector() {
	}

	@Override
	public List<String> collectErrorList(Document result) {
		List<String> errorList = new ArrayList<>();
		NodeList errorTagList = result.getElementsByTagName("Error");
		for (int i = 0; i < errorTagList.getLength(); i++) {
			Node errorItem = errorTagList.item(i);

			String pattern = findNestedTagValue(errorItem, "Pattern");
			String xpath = findNestedTagValue(errorItem, "Xpath");
			String message = findNestedTagValue(errorItem, "Description");

			if (DUMP_NODE_VALUE_INSTEAD_OF_MESSAGE) {
				String nodeToString = nodeToString(errorItem);
				System.out.println(nodeToString);
				errorList.add(nodeToString);
				continue;
			}

			StringBuilder sb = new StringBuilder();
			if (message != null) {
				sb.append(message.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\\s+", " "));
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d) %s\n\tpattern = %s\n\txpath = %s", i, message, pattern, xpath));
			}
			
			errorList.add(sb.toString());
		}
		return errorList;
	}

	private String findNestedTagValue(Node parentNode, String tagName) {
		NodeList childNodes = parentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (tagName.equals(node.getLocalName())) {
				return getNodeTextValue(node);
			}
		}
		return null;
	}

	private String getNodeTextValue(Node item) {
		if (item != null) {
			for (int j = 0; j < item.getChildNodes().getLength(); j++) {
				Node child = item.getChildNodes().item(j);
				if (child.getNodeType() == Node.TEXT_NODE) {
					return child.getNodeValue();
				}
			}
		}
		return null;
	}

	private String nodeToString(Node node) {
		String res;
		StringWriter writer = new StringWriter();
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(node), new StreamResult(writer));
			res = writer.toString();
		} catch (Exception e) {
			log.error("Failed to convert node to string", e);
			res = String.valueOf(node);
		}
		return res;
	}
}
