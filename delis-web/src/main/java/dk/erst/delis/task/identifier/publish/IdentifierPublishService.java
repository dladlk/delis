package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class IdentifierPublishService {

    protected final Log log = LogFactory.getLog(getClass());
    private final SmpXmlService smpXmlService;

    @Autowired
    public IdentifierPublishService(SmpXmlService smpXmlService) {
        this.smpXmlService = smpXmlService;
    }

    public PublishReport publish(Identifier identifier, SmpEndpoint endpoint){
        String serviceGroupXml = smpXmlService.createServiceGroupXml(identifier);
        String serviceMetadataXml = smpXmlService.createServiceMetadataXml(identifier);
        boolean serviceGroupCreated = doCall("PUT", endpoint, createServiceGroupEndpointPath(identifier), serviceGroupXml);
        boolean serviceMetadataCreated = doCall("PUT", endpoint, createServiceMetadataEndpointPath(identifier), serviceMetadataXml);
        return new PublishReport(serviceGroupCreated, serviceMetadataCreated);
    }

    private String createServiceGroupEndpointPath(Identifier identifier) {
        return identifier.getType() + "::" + identifier.getValue();
    }

    private String createServiceMetadataEndpointPath(Identifier identifier) {
        return identifier.getType() + "::" + identifier.getValue() + "/services/" + "connectivity-docid-qns::doc_id1";
    }

    private boolean doCall(String method, SmpEndpoint endpoint, String path, String xml) {
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
