package dk.erst.delis.task.document.parse;

import dk.erst.delis.task.document.parse.cachingtransformerfactory.DelisTransformerFactory;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.StandardErrorListener;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Slf4j
public class XSLTUtil {

	public static void apply(InputStream xslStream, Path xslFilePath, InputStream xmlStream, OutputStream resultStream) throws Exception {
		Transformer transformer = getTransformer(xslStream, xslFilePath);

		StreamResult streamResult = new StreamResult(resultStream);
		transformer.transform(new StreamSource(xmlStream), streamResult);
	}

	private static Transformer getTransformer(InputStream xslStream, Path xslFilePath) throws Exception {
		return buildTransformer(xslStream, xslFilePath, DelisTransformerFactory.getInstance());
	}
	
	protected static Transformer buildTransformer(InputStream xslStream, Path xslFilePath, TransformerFactory transFactory) throws Exception {
		long start = System.currentTimeMillis();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder newDocumentBuilder = factory.newDocumentBuilder();
		Document xsltDocument = newDocumentBuilder.parse(xslStream);

		StandardErrorListener listener = new StandardErrorListener();
		listener.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);

		transFactory.setErrorListener(listener);
		DOMSource source = new DOMSource(xsltDocument);
		
		if (xslFilePath != null) {
			/*
			 * It is critical for relative paths in XSLT - e.g. in
			 * BIS-Billing_2_OIOUBL_MASTER.xslt
			 */
			source.setSystemId(xslFilePath.toString());
		}
		Transformer transformer = transFactory.newTransformer(source);
		
		log.info("Built transformer by " + xslFilePath + " in " + (System.currentTimeMillis() - start) + " ms");
	
		return transformer;
	}
}
