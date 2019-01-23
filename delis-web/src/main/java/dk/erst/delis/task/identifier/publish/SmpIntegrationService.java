package dk.erst.delis.task.identifier.publish;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.SmpEndpointConfig;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmpIntegrationService {

	private ConfigBean configBean;

	@Autowired
	public SmpIntegrationService(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public boolean create(String path, String xml) {
		String method = "PUT";
		return call(method, path, xml);
	}

	public boolean delete(String path) {
		String method = "DELETE";
		return call(method, path, null);
	}

	private boolean call(String method, String path, String xml) {
		SmpEndpointConfig endpoint = configBean.getSmpEndpointConfig();
		String url = endpoint.getUrl() + "/" + path;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod(method);
			String encoded = Base64.getEncoder().encodeToString((endpoint.getUserName() + ":" + endpoint.getPassword()).getBytes(StandardCharsets.UTF_8));
			con.setRequestProperty("Authorization", "Basic " + encoded);
			con.addRequestProperty("Content-Type", "text/xml");

			if (xml != null) {
				con.setDoOutput(true);
				con.getOutputStream().write(xml.getBytes(StandardCharsets.UTF_8));
			}

			log.info(method + " to " + url + " with " + (xml != null ? xml.length() : -1) + " bytes xml");
			
			if (log.isDebugEnabled()) {
				log.debug(xml);
			}
			
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
