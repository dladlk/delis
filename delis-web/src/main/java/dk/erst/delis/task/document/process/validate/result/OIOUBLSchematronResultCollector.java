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
	public static boolean DUMP_LOCATION = false;

	protected static OIOUBLSchematronResultCollector INSTANCE = new OIOUBLSchematronResultCollector();

	private OIOUBLSchematronResultCollector() {
	}

	@Override
	public List<String> collectErrorList(Document result) {
		List<String> errorList = null;
		NodeList errorTagList = result.getElementsByTagName("Description");
		for (int i = 0; i < errorTagList.getLength(); i++) {
			if (errorList == null) {
				errorList = new ArrayList<String>();
			}
			Node item = errorTagList.item(i);

			if (DUMP_NODE_VALUE_INSTEAD_OF_MESSAGE) {
				errorList.add(nodeToString(item));
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
			if (message != null) {
				sb.append(message.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\\s+", " "));
			}
			errorList.add(sb.toString());
		}
		return errorList;
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
