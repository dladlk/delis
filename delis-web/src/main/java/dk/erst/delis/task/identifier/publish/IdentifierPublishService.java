package dk.erst.delis.task.identifier.publish;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.task.identifier.publish.bdxr.SmpXmlService;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

@Service
public class IdentifierPublishService {

    private final SmpXmlService smpXmlService;
    private final SmpIntegrationService smpIntegrationService;

    @Autowired
    public IdentifierPublishService(SmpXmlService smpXmlService, SmpIntegrationService smpIntegrationService) {
        this.smpXmlService = smpXmlService;
		this.smpIntegrationService = smpIntegrationService;
    }
    
    public boolean publishIdentifier(Identifier identifier) {
    	ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of(identifier.getUniqueValueType());
    	if (!publishServiceGroup(participantIdentifier, null, null)) {
    		return false;
    	}
    	if (!publishServiceMetadata(participantIdentifier, null, null)) {
    		return false;
    	}
    	return true;
    }

    protected boolean publishServiceGroup(ParticipantIdentifier participantIdentifier, PublishProperties publishProperties, SmpEndpoint endpoint) {
        String serviceGroupXml = smpXmlService.createServiceGroupXml(participantIdentifier, publishProperties);
        return smpIntegrationService.create(endpoint, createServiceGroupEndpointPath(participantIdentifier), serviceGroupXml);

    }

    protected boolean publishServiceMetadata(ParticipantIdentifier participantIdentifier, PublishProperties publishProperties, SmpEndpoint endpoint) {
        String serviceMetadataXml = smpXmlService.createServiceMetadataXml(participantIdentifier, publishProperties);
        return smpIntegrationService.create(endpoint, createServiceMetadataEndpointPath(participantIdentifier, publishProperties), serviceMetadataXml);
    }

    private String createServiceGroupEndpointPath(ParticipantIdentifier participantIdentifier) {
        String path = participantIdentifier.getScheme().toString() + "::" + participantIdentifier.getIdentifier();
        return encodeUrl(path);
    }
    
    private String createServiceMetadataEndpointPath(ParticipantIdentifier participantIdentifier, PublishProperties publishProperties) {
        String serviceId = publishProperties.getDocumentIdentifierScheme() + "::" + publishProperties.getDocumentIdentifierValue();
        return createServiceGroupEndpointPath(participantIdentifier) + "/services/" + encodeUrl(serviceId);
    }

    private String encodeUrl(String value) {
    	try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception at encodeUrl to UTF-8 for value: "+value, e);
		}
    }
}
