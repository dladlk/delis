package dk.erst.delis.task.identifier.publish;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmpIntegrationService {

	public boolean create(SmpEndpoint endpoint, String path, String xml) {
		String method = "PUT";
		String url = endpoint.getEndpointUrl() + "/" + path;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod(method);
			String encoded = Base64.getEncoder().encodeToString((endpoint.getUserName() + ":" + endpoint.getPassword()).getBytes(StandardCharsets.UTF_8));
			con.setRequestProperty("Authorization", "Basic " + encoded);
			con.addRequestProperty("Content-Type", "text/xml");

			con.setDoOutput(true);
			con.getOutputStream().write(xml.getBytes(StandardCharsets.UTF_8));

			log.info(method + " to " + url + " with " + xml.length() + " bytes xml");
			int status = con.getResponseCode();
			con.disconnect();
			log.info(method + " result status: " + status);

			return status >= 200 && status < 300;
		} catch (IOException e) {
			log.error("Failed to " + method + " " + url, e);
			return false;
		}
	}

}
