package dk.erst.delis.task.document.parse;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;


public class XSLTUtil {

	public static void apply(InputStream xslStream, InputStream xmlStream, OutputStream resultStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document xsltDocument = factory.newDocumentBuilder().parse(xslStream);


		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer transformer = transFact.newTransformer(new DOMSource(xsltDocument));

		StreamResult streamResult = new StreamResult(resultStream);
		transformer.transform(new StreamSource(xmlStream), streamResult);

	}
}
