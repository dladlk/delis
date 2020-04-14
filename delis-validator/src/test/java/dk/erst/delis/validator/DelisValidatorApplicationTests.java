package dk.erst.delis.validator;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class DelisValidatorApplicationTests {

	@Value("${local.server.port}")
	private int port;
	private String postUrl;

	@Test
	public void validate() throws IOException {
		postUrl = "http://localhost:" + port + "/rest/validate";

		ValidatorClientUsage usage = new ValidatorClientUsage(postUrl);
		usage.testRunningService();
	}

}
