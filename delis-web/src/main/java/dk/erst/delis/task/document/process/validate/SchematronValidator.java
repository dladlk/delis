package dk.erst.delis.task.document.process.validate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.process.validate.result.ISchematronResultCollector;

public class SchematronValidator {

	public List<String> validate(InputStream xmlStream, InputStream schematronStream, ISchematronResultCollector collector) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XSLTUtil.apply(schematronStream, null, xmlStream, baos);

		DocumentBuilderFactory factoryNoNS = DocumentBuilderFactory.newInstance();
		factoryNoNS.setNamespaceAware(true);
		Document result = factoryNoNS.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

		List<String> errorList = collector.collectErrorList(result);
		return errorList;
	}

}
