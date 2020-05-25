package dk.erst.delis.validator.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.TestCaseProvider;
import dk.erst.delis.validator.TestCaseProvider.TestCaseData;
import dk.erst.delis.validator.service.input.FileBasedFileInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateServiceTest {

	private String root = "../delis-resources/validation";

	@Test
	public void testValidateFile() throws IOException {
		DelisValidatorConfig config = new DelisValidatorConfig();
		config.setStorageValidationRoot(root);

		PersistService ps = new PersistService(config);
		ValidateStatBean stat = new ValidateStatBean();
		ValidateService s = new ValidateService(config, ps, stat);

		for (TestCaseData tc : TestCaseProvider.getTestCaseList()) {

			log.info("Validating file " + tc.getDescription() + ", expect " + (tc.isErrorExpected() ? "ERROR" : "OK"));

			ValidateResult result = s.validateFile(new FileBasedFileInput(tc.getFile()), false);
			System.out.println(result);
			assertNotNull(result);
			assertEquals(!tc.isErrorExpected(), result.getProcessLog().isSuccess());
		}

		System.out.println(stat);
	}

}
