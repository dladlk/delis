package dk.erst.delis.task.document.process.validate;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.process.DocumentValidationTransformationServiceUnitTest;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;

public class SchemaValidatorTest {

	@Test
	public void test() throws IOException, SAXException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final TestDocument testDocument = TestDocument.ERROR_XSD_BIS3_INVOICE;
		try (InputStream is = testDocument.getInputStream()) {
			IOUtils.copy(is, baos);
		}
		String xml = new String(baos.toByteArray());

		RuleService rs = DocumentValidationTransformationServiceUnitTest.buildDefaultRuleService();

		List<RuleDocumentValidation> defaultValidationRuleList = rs.getValidationList();
		List<RuleDocumentValidation> xsdValidation = defaultValidationRuleList.stream().filter(v -> v.getValidationType().isXSD()).collect(Collectors.toList());

		RuleDocumentValidation xsdTestFormatValidator = xsdValidation.stream().filter(v -> v.getDocumentFormat() == testDocument.getDocumentFormat()).findFirst().get();

		SchemaValidator v = new SchemaValidator();
		List<ErrorRecord> validate = v.validate(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), rs.filePath(xsdTestFormatValidator), xsdTestFormatValidator);
		assertEquals(1, validate.size());
		ErrorRecord er = validate.get(0);
		assertEquals("cvc-complex-type.2.4.a", er.getCode());
		assertEquals("Invalid content was found starting with element 'cbc:IssueDate'.", er.getMessage());
		assertEquals(DocumentErrorCode.BIS3_XSD, er.getErrorType());
		assertEquals("error", er.getFlag());
		assertEquals("/Invoice, line 6, column 18", er.getDetailedLocation());
		assertEquals("/Invoice", er.getLocation());
	}

}
