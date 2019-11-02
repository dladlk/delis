package dk.erst.delis.task.document.process.validate.result;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OIOUBLSchematronResultCollector implements ISchematronResultCollector {

	public static final String ERROR = "Error";

	protected static OIOUBLSchematronResultCollector INSTANCE = new OIOUBLSchematronResultCollector();

	private OIOUBLSchematronResultCollector() {
	}

	@Override
	public List<ErrorRecord> collectErrorList(Document result) {
		List<ErrorRecord> errorList = new ArrayList<>();
		NodeList errorTagList = result.getElementsByTagName(ERROR);
		for (int i = 0; i < errorTagList.getLength(); i++) {
			Node errorItem = errorTagList.item(i);

			String pattern = findNestedTagValue(errorItem, "Pattern");
			String xpath = findNestedTagValue(errorItem, "Xpath");
			String message = findNestedTagValue(errorItem, "Description").trim();

			String code = "";
			if (message.startsWith("[")) {
				int index = message.indexOf("]");
				if (index > 0) {
					String tmp = message;
					message = tmp.substring(index + 1);
					code = tmp.substring(1, index);
				}
			}

			StringBuilder sb = new StringBuilder();
			if (message != null) {
				sb.append(message.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\\s+", " "));
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d) %s\n\tpattern = %s\n\txpath = %s", i, message, pattern, xpath));
			}
			
			errorList.add(new ErrorRecord(DocumentErrorCode.OIOUBL_SCH, code, message, ERROR, xpath));
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
}
