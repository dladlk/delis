package dk.erst.delis.task.identifier.publish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    public boolean publishServiceGroup(PublishProperties publishProperties, SmpEndpoint endpoint) {
        String serviceGroupXml = smpXmlService.createServiceGroupXml(publishProperties);
        return doCall("PUT", endpoint, createServiceGroupEndpointPath(publishProperties), serviceGroupXml);

    }

    public boolean publishServiceMetadata(PublishProperties publishProperties, SmpEndpoint endpoint) {
        String serviceMetadataXml = smpXmlService.createServiceMetadataXml(publishProperties);
        return doCall("PUT", endpoint, createServiceMetadataEndpointPath(publishProperties), serviceMetadataXml);
    }

    private String createServiceGroupEndpointPath(PublishProperties publishProperties) {
        String path = publishProperties.getParticipantIdentifierScheme() + "::" + publishProperties.getParticipantIdentifierValue();
        try {
            return URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        return null;
    }

    private String createServiceMetadataEndpointPath(PublishProperties publishProperties) {
        String participantIdentifier = publishProperties.getParticipantIdentifierScheme() + "::" + publishProperties.getParticipantIdentifierValue();
        String documentIdentifier = publishProperties.getDocumentIdentifierScheme() + "::" + publishProperties.getDocumentIdentifierValue();
        try {
            return URLEncoder.encode(participantIdentifier, "UTF-8") + "/services/" + URLEncoder.encode(documentIdentifier, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        return null;
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
