package dk.erst.delis.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dk.erst.delis.validator.TestCaseProvider.TestCaseData;
import dk.erst.delis.validator.client.ValidatorClient;
import dk.erst.delis.validator.client.ValidatorClient.ValidatorClientResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidatorClientUsage {

	private ValidatorClient client;
	private String postUrl;

	public ValidatorClientUsage(String postUrl) {
		this.postUrl = postUrl;
		this.client = new ValidatorClient(postUrl);
	}

	public void testRunningService() throws IOException {
		log.info("Testing against " + postUrl);

		long start = System.currentTimeMillis();

		byte[] bytes = "NON XML DATA".getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

		String textFileName = "testFileName";

		assertFalse(sendRestValidateRequest(inputStream, textFileName).isValid());

		List<TestCaseData> testCaseList = TestCaseProvider.getTestCaseList();
		int countValid = 0;
		for (TestCaseData tc : testCaseList) {

			log.info("Validating file " + tc.getDescription() + ", expect " + (tc.isErrorExpected() ? "ERROR" : "OK"));
			String fileName = tc.getDescription();
			boolean valid = sendRestValidateRequest(tc.getInputStream(), fileName).isValid();
			if (valid) {
				countValid++;
			}
			if (tc.isErrorExpected()) {
				assertFalse(valid);
			} else {
				assertTrue(valid);
			}
		}

		long duration = System.currentTimeMillis() - start;
		int totalCount = testCaseList.size();
		int avgPerFile = (int) (duration / totalCount);
		log.info("Done testing service " + postUrl + " in " + duration + " ms, executed " + totalCount + " tests, countValid = " + countValid + ", average ms per file: " + avgPerFile + " ms");
	}

	private ValidatorClientResult sendRestValidateRequest(InputStream inputStream, String name) throws IOException {
		long start = System.currentTimeMillis();
		ValidatorClientResult r = this.client.validate(inputStream, name);

		log.info("Loaded result in " + (System.currentTimeMillis() - start) + " ms: " + r);

		return r;
	}
}
