package dk.erst.delis.task.document.parse;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.StandardErrorListener;


public class XSLTUtil {

	public static void apply(InputStream xslStream, Path xslFilePath, InputStream xmlStream, OutputStream resultStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder newDocumentBuilder = factory.newDocumentBuilder();
		Document xsltDocument = newDocumentBuilder.parse(xslStream);

		StandardErrorListener listener = new StandardErrorListener();
		listener.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);

		TransformerFactory transFact = TransformerFactory.newInstance();
		transFact.setErrorListener(listener);
		DOMSource source = new DOMSource(xsltDocument);
		if (xslFilePath != null) {
			/*
			 * It is critical for relative paths in XSLT - e.g. in BIS-Billing_2_OIOUBL_MASTER.xslt
			 */
			source.setSystemId(xslFilePath.toString());
		}
		Transformer transformer = transFact.newTransformer(source);

		StreamResult streamResult = new StreamResult(resultStream);
		transformer.transform(new StreamSource(xmlStream), streamResult);
	}
}
