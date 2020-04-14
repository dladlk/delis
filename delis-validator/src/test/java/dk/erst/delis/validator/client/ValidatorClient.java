package dk.erst.delis.validator.client;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.util.StreamUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidatorClient implements Closeable {

	private CloseableHttpClient client;

	private String validationUrl;

	public ValidatorClient(String validationUrl) {
		long start = System.currentTimeMillis();
		this.validationUrl = validationUrl;
		this.client = HttpClientBuilder.create().build();
		log.info("Initialized HttpClient for validation url " + validationUrl + " in " + (System.currentTimeMillis() - start) + " ms");
	}

	@Override
	public void close() throws IOException {
		if (client != null) {
			client.close();
		}
	}

	public ValidatorClientResult validate(InputStream inputStream, String fileName) throws IOException {
		HttpPost post = new HttpPost(validationUrl);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", inputStream, ContentType.DEFAULT_BINARY, fileName);

		long start = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug("Start sending " + fileName + " to " + this.validationUrl);
		}

		post.setEntity(builder.build());

		ValidatorClientResult r = new ValidatorClientResult();
		try (CloseableHttpResponse response = client.execute(post)) {

			int statusCode = response.getStatusLine().getStatusCode();

			r.setStatusCode(statusCode);

			if (response.getEntity() != null && response.getEntity().getContent() != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				StreamUtils.copy(response.getEntity().getContent(), baos);
				r.setMessage(new String(baos.toByteArray(), StandardCharsets.UTF_8));
			}

			Header header = Arrays.stream(response.getHeaders("description")).findFirst().orElse(null);
			if (header != null) {
				r.setDescription(header.getValue());
			}

			r.setValid(statusCode == 200);
			return r;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("Done sending of " + fileName + " in " + (System.currentTimeMillis() - start) + " ms with result " + r);
			}
		}
	}

	@Data
	public static class ValidatorClientResult {
		private String message;
		private String description;
		private int statusCode;
		private boolean valid;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(valid ? "VALID" : "INVALID");
			sb.append(" ");
			sb.append("Response HTTP status: ");
			sb.append(statusCode);
			if (description != null) {
				sb.append(", description: ");
				sb.append(description);
			}
			if (message != null && message.length() > 0) {
				sb.append(", message: ");
				sb.append(message);
			}
			return sb.toString();
		}
	}
}
