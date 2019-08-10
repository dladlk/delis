package dk.erst.delis.task.document.process.validate.result;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;

public class SchematronResultCollectorFactory {

	public static ISchematronResultCollector getCollector(DocumentFormat df) {
		if (df.getDocumentFormatFamily() == DocumentFormatFamily.OIOUBL) {
			return OIOUBLSchematronResultCollector.INSTANCE;
		}
		return new SVRLSchematronResultCollector(df);
	}
}
