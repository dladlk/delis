package dk.erst.delis.task.document.process.validate.result;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SVRLSchematronResultCollector implements ISchematronResultCollector {

	private DocumentFormat documentFormat;

	protected SVRLSchematronResultCollector(DocumentFormat documentFormat) {
		this.documentFormat = documentFormat;
	}

	public List<ErrorRecord> collectErrorList(Document result) {
		List<ErrorRecord> errorList = new ArrayList<>();
		NodeList failedAssertList = result.getElementsByTagNameNS("http://purl.oclc.org/dsdl/svrl", "failed-assert");
		for (int i = 0; i < failedAssertList.getLength(); i++) {
			Node item = failedAssertList.item(i);

			String id = getAttribute(item, "id");
			String flag = getAttribute(item, "flag");
			String location = getAttribute(item, "location");
			
			String message = "";
			for (int j = 0; j < item.getChildNodes().getLength(); j++) {
				Node child = item.getChildNodes().item(j);
				if ("text".equals(child.getLocalName())) {
					message = child.getChildNodes().item(0).getNodeValue();
					break;
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("%d) [%s] %s\n\tlocation = %s", i, flag, message, location));
			}
			
			DocumentErrorCode errorCode = documentFormat.isCII() ? DocumentErrorCode.CII_SCH : DocumentErrorCode.BIS3_SCH;
			errorList.add(new ErrorRecord(errorCode, id, message, flag, location));
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

}
