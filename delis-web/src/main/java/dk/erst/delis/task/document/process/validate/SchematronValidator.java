package dk.erst.delis.task.document.process.validate;

import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.process.validate.result.ISchematronResultCollector;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class SchematronValidator {
	
	private static final boolean DUMP_RAW_RESULT = false;

	public List<ErrorRecord> validate(InputStream xmlStream, InputStream schematronStream, ISchematronResultCollector collector) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XSLTUtil.apply(schematronStream, null, xmlStream, baos);

		DocumentBuilderFactory factoryNoNS = DocumentBuilderFactory.newInstance();
		factoryNoNS.setNamespaceAware(true);
		byte[] byteArray = baos.toByteArray();
		
		if (DUMP_RAW_RESULT) {
			log.debug(new String(byteArray, StandardCharsets.UTF_8));
		}
		
		Document result = factoryNoNS.newDocumentBuilder().parse(new ByteArrayInputStream(byteArray));

		List<ErrorRecord> errorList = collector.collectErrorList(result);
		return errorList;
	}

}
