package dk.erst.delis.document.sbdh;

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlUtil {

	public static void serialize(Document doc, OutputStream out) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(domSource, new StreamResult(out));
	}
}
