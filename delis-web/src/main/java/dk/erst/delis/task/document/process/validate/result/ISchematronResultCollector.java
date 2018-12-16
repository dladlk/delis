package dk.erst.delis.task.document.process.validate.result;

import java.util.List;

import org.w3c.dom.Document;

public interface ISchematronResultCollector {

	List<String> collectErrorList(Document result);

}
