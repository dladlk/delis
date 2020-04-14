package dk.erst.delis.validator;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test to check existing running service.
 * 
 * Just change testUrl to corresponding
 */
public class ValidatorClientUsageTest {

	private String testUrl = "http://localhost:8080/rest/validate";

	@Test
	@Ignore
	public void test() throws IOException {
		ValidatorClientUsage u = new ValidatorClientUsage(testUrl);
		u.testRunningService();
	}

}
