package dk.erst.delis.task.document.process.validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Slf4j
public class SchemaValidator {

	public List<ErrorRecord> validate(InputStream xmlStream, Path schemaFileName, RuleDocumentValidation ruleDocumentValidation) throws IOException, SAXException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        List<ErrorRecord> errors = new ArrayList<>();

		Schema schema;
        try {
            try {
                URL url = this.getClass().getResource(schemaFileName.toString());
                schema = factory.newSchema(url);
            } catch (Exception var9) {
                File file = schemaFileName.toFile();
                schema = factory.newSchema(file);
            }

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlStream));
        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException ex = (SAXParseException)e;
                String location = "line " + ex.getLineNumber() + ", column " + ex.getColumnNumber();
                log.error("Failed validation of file " + schemaFileName + ", location: " + location + " by rule " + ruleDocumentValidation, ex);

                ErrorRecord err = new ErrorRecord(ruleDocumentValidation.buildErrorCode(), "", ex.getMessage(), "error", location);
                errors.add(err);
            } else {
                throw e;
            }
        }
        return errors;
    }
}
