package dk.erst.delis.task.document.process.validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import dk.erst.delis.data.entities.journal.IErrorInfo;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.process.validate.sax.CurrentLocationContentHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchemaValidator {

	private static final boolean DETECT_LOCATION = true;

	@SuppressWarnings("resource")
	public List<ErrorRecord> validate(InputStream xmlStream, Path schemaFileName, RuleDocumentValidation ruleDocumentValidation) throws IOException, SAXException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		List<ErrorRecord> errors = new ArrayList<>();

		Schema schema = null;
		CurrentLocationContentHandler locationHandler = null;
		if (DETECT_LOCATION) {
			locationHandler = new CurrentLocationContentHandler();
		}
		try {
			try {
				URL url = this.getClass().getResource(schemaFileName.toString());
				if (url != null) {
					schema = factory.newSchema(url);
				}
			} catch (Exception var9) {
				/*
				 * Fails if schema is not a resource but a file
				 */
			}
			if (schema == null) {
				schema = factory.newSchema(schemaFileName.toFile());
			}
			if (DETECT_LOCATION) {
				ValidatorHandler validatorHandler = schema.newValidatorHandler();
				validatorHandler.setContentHandler(locationHandler);
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setNamespaceAware(true);
				XMLReader xmlReader = parserFactory.newSAXParser().getXMLReader();
				xmlReader.setContentHandler(validatorHandler);
				xmlReader.parse(new InputSource(new CloseShieldInputStream(xmlStream)));
			} else {
				Validator validator = schema.newValidator();
				validator.validate(new StreamSource(new CloseShieldInputStream(xmlStream)));
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			if (e instanceof SAXParseException) {
				SAXParseException ex = (SAXParseException) e;

				String currentLocation = "";
				if (locationHandler != null) {
					currentLocation = locationHandler.getCurrentLocation();
				}

				StringBuilder sb = new StringBuilder();
				if (StringUtils.isNotBlank(currentLocation)) {
					sb.append(currentLocation);
					sb.append(", ");
				}
				sb.append("line ");
				sb.append(ex.getLineNumber());
				sb.append(", column ");
				sb.append(ex.getColumnNumber());
				String detailedLocation = sb.toString();

				log.error("Failed validation of file " + schemaFileName + ", location: " + detailedLocation + " by rule " + ruleDocumentValidation, ex);

				ErrorRecord err = new ErrorRecord(ruleDocumentValidation.buildErrorCode(), "", ex.getMessage(), "error", currentLocation, detailedLocation);
				cleanupXsdErrorRecord(err);
				errors.add(err);
			} else {
				throw e;
			}
		}
		return errors;
	}

	public static void cleanupXsdErrorRecord(IErrorInfo er) {
		String message = er.getMessage();
		String code = er.getCode();

		if (message.startsWith("cvc-") && StringUtils.isEmpty(code)) {
			int cvcCodeEnd = message.indexOf(": ");
			if (cvcCodeEnd > 0) {
				er.setCode(message.substring(0, cvcCodeEnd));
				er.setMessage(message.substring(cvcCodeEnd + 2));
			}
		}

		message = er.getMessage();
		int oneOfIndex = message.indexOf(" One of ");
		if (oneOfIndex > 0) {
			er.setMessage(message.substring(0, oneOfIndex));
		}

	}
}
