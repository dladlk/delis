package dk.erst.delis.task.document.process.validate.result;

import org.w3c.dom.Document;

import java.util.List;

public interface ISchematronResultCollector {

	List<ErrorRecord> collectErrorList(Document result);

}
