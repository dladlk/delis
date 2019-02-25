package dk.erst.delis.task.document.process.validate.result;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.erst.delis.data.enums.document.DocumentErrorCode;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BIS3SchematronResultCollector implements ISchematronResultCollector {

	public static boolean DUMP_NODE_VALUE_INSTEAD_OF_MESSAGE = false;
	public static boolean DUMP_LOCATION = false;

	protected static BIS3SchematronResultCollector INSTANCE = new BIS3SchematronResultCollector();

	private BIS3SchematronResultCollector() {
	}

	public List<ErrorRecord> collectErrorList(Document result) {
		List<ErrorRecord> errorList = new ArrayList<>();
		NodeList failedAssertList = result.getElementsByTagNameNS("http://purl.oclc.org/dsdl/svrl", "failed-assert");
		for (int i = 0; i < failedAssertList.getLength(); i++) {
			Node item = failedAssertList.item(i);

			String id = getAttribute(item, "id");
			String flag = getAttribute(item, "flag");
			String location = getAttribute(item, "location");

			if (DUMP_NODE_VALUE_INSTEAD_OF_MESSAGE) {
				String nodeToString = nodeToString(item);
				System.out.println(nodeToString);
				errorList.add(new ErrorRecord(DocumentErrorCode.BIS3_SCH, id, nodeToString, flag, location));
				continue;
			}

			String message = "";
			for (int j = 0; j < item.getChildNodes().getLength(); j++) {
				Node child = item.getChildNodes().item(j);
				if ("text".equals(child.getLocalName())) {
					message = child.getChildNodes().item(0).getNodeValue();
					break;
				}
			}
			StringBuilder sb = new StringBuilder();
			if (flag != null) {
				sb.append(flag);
				sb.append(" ");
			}
			if (id != null) {
				sb.append("[");
				sb.append(id);
				sb.append("]\t");
			}
			if (message != null) {
				message = message.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\\s+", " ");
				sb.append(message);
			}
			if (DUMP_LOCATION && location != null) {
				sb.append(" LOCATION: " + location);
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d) [%s] %s\n\tlocation = %s", i, flag, message, location));
			}
			
			errorList.add(new ErrorRecord(DocumentErrorCode.BIS3_SCH, id, message, flag, location));
		}
		return errorList;
	}

	private String getAttribute(Node item, String attribute) {
		Node namedItem = item.getAttributes().getNamedItem(attribute);
		if (namedItem != null) {
			return namedItem.getNodeValue();
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
