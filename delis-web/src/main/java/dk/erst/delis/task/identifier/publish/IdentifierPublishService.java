package dk.erst.delis.task.identifier.publish;

import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.task.identifier.publish.bdxr.SmpXmlService;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

/*
 * Orchestration service, responsible for decision which action should be taken (PUT or DELETE), 
 * 
 * and which invokes other services, responsible for:
 * 
 * - loading expected data to be published (IdentifierPublishDataService), 
 * 
 * - build XML to publish (SmpXmlService)
 * 
 * - actuall make publishing in SMP (SmpIntegrationService)
 */
@Service
public class IdentifierPublishService {

	private final SmpXmlService smpXmlService;
	private final SmpIntegrationService smpIntegrationService;
	private final IdentifierPublishDataService identifierPublishDataService;
	private final SmpLookupService smpLookupService;

	@Autowired
	public IdentifierPublishService(SmpXmlService smpXmlService, SmpIntegrationService smpIntegrationService, IdentifierPublishDataService identifierPublishDataService, SmpLookupService smpLookupService) {
		this.smpXmlService = smpXmlService;
		this.smpIntegrationService = smpIntegrationService;
		this.identifierPublishDataService = identifierPublishDataService;
		this.smpLookupService = smpLookupService;
	}

	public boolean publishIdentifier(Identifier identifier) {

		SmpPublishData publishData = identifierPublishDataService.buildPublishData(identifier);

		if (identifier.getStatus().isDeleted()) {
			return deleteServiceGroup(publishData.getParticipantIdentifier());
		}

		if (!publishServiceGroup(publishData.getParticipantIdentifier(), publishData)) {
			return false;
		}

		/*
		 * TODO: We need to synchronize somehow list of published service metadata info
		 * with expected (loaded by identifierPublishDataService)
		 */
		
		SmpPublishData currentPublishData = smpLookupService.lookup(publishData.getParticipantIdentifier());
		
		/*
		 * TODO: Compare currentPublishData with expected and define what should be actually done:
		 * 
		 *  1) Some services should be updated/created
		 *  
		 *  2) Some services should be deleted
		 */

		List<SmpPublishServiceData> serviceList = publishData.getServiceList();
		for (SmpPublishServiceData smpPublishServiceData : serviceList) {
			if (!publishServiceMetadata(publishData.getParticipantIdentifier(), smpPublishServiceData)) {
				return false;
			}
		}
		return true;
	}

	protected boolean deleteServiceGroup(ParticipantIdentifier participantIdentifier) {
		return smpIntegrationService.delete(createServiceGroupEndpointPath(participantIdentifier));
	}

	protected boolean deleteServiceMetadata(ParticipantIdentifier participantIdentifier, SmpPublishServiceData serviceData) {
		return smpIntegrationService.delete(createServiceMetadataEndpointPath(participantIdentifier, serviceData));
	}

	protected boolean publishServiceGroup(ParticipantIdentifier participantIdentifier, SmpPublishData smpPublishData) {
		String serviceGroupXml = smpXmlService.createServiceGroupXml(participantIdentifier, smpPublishData);
		return smpIntegrationService.create(createServiceGroupEndpointPath(participantIdentifier), serviceGroupXml);

	}

	protected boolean publishServiceMetadata(ParticipantIdentifier participantIdentifier, SmpPublishServiceData serviceData) {
		String serviceMetadataXml = smpXmlService.createServiceMetadataXml(participantIdentifier, serviceData);
		return smpIntegrationService.create(createServiceMetadataEndpointPath(participantIdentifier, serviceData), serviceMetadataXml);
	}

	private String createServiceGroupEndpointPath(ParticipantIdentifier participantIdentifier) {
		String path = participantIdentifier.getScheme().toString() + "::" + participantIdentifier.getIdentifier();
		return encodeUrl(path);
	}

	private String createServiceMetadataEndpointPath(ParticipantIdentifier participantIdentifier, SmpPublishServiceData serviceData) {
		String serviceId = serviceData.buildServiceId();
		return createServiceGroupEndpointPath(participantIdentifier) + "/services/" + encodeUrl(serviceId);
	}

	private String encodeUrl(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception at encodeUrl to UTF-8 for value: " + value, e);
		}
	}
}
