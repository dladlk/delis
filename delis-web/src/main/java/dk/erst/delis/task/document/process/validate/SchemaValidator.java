package dk.erst.delis.task.document.process.validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class SchemaValidator {

	public void validate(InputStream xmlStream, Path schemaFileName) throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		Schema schema;
		try {
			URL url = this.getClass().getResource(schemaFileName.toString());
			schema = factory.newSchema(url);
		} catch (Exception var9) {
			File file = schemaFileName.toFile();
			schema = factory.newSchema(file);
		}

		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(xmlStream));
	}
}
