package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.xml.SmpXmlServiceFactory;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;

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
@Slf4j
public class IdentifierPublishService {

	private final SmpXmlServiceFactory smpXmlServiceFactory;
	private final SmpIntegrationService smpIntegrationService;
	private final IdentifierPublishDataService identifierPublishDataService;
	private final SmpLookupService smpLookupService;
	@Autowired
	private JournalIdentifierDaoRepository journalIdentifierDaoRepository;

	@Autowired
	public IdentifierPublishService(SmpXmlServiceFactory smpXmlServiceFactory, SmpIntegrationService smpIntegrationService, IdentifierPublishDataService identifierPublishDataService, SmpLookupService smpLookupService) {
		this.smpXmlServiceFactory = smpXmlServiceFactory;
		this.smpIntegrationService = smpIntegrationService;
		this.identifierPublishDataService = identifierPublishDataService;
		this.smpLookupService = smpLookupService;
	}

	public boolean publishIdentifier(Identifier identifier) {
		SmpPublishData forPublish = identifierPublishDataService.buildPublishData(identifier);
		SmpPublishData published = smpLookupService.lookup(forPublish.getParticipantIdentifier());
		if(published == null) {
			log.info(String.format("ServiceGroup by Identifier '%s' is not found", identifier.getValue()));
			return false;
		}
		if (identifier.getStatus().isDeleted()) {
			boolean isDeleted = deleteServiceGroup(forPublish.getParticipantIdentifier());
			if(isDeleted) {
				addJournalIdentifierRecord(identifier, "Service group was deleted", journalIdentifierDaoRepository);
			}
			return isDeleted;
		}
		if(!isPublishDataValid(forPublish)) {
			log.info(String.format("Publish data created for identifier '%s' is invalid!", identifier.getValue()));
			return false;
		}
		if (!publishServiceGroup(forPublish.getParticipantIdentifier(), forPublish)) {
			addJournalIdentifierRecord(identifier, "Unable to publish service group", journalIdentifierDaoRepository);
			return false;
		}
		for (SmpPublishServiceData publishedService : published.getServiceList()) {
			if(!contains(publishedService, forPublish.getServiceList())) {
				boolean isDeleted = deleteServiceMetadata(published.getParticipantIdentifier(), publishedService);
				if(isDeleted) {
					SmpDocumentIdentifier documentIdentifier = publishedService.getDocumentIdentifier();
					String message = String.format("Service metadata with DocumentIdentifier value '%s' was deleted", documentIdentifier.getDocumentIdentifierValue());
					addJournalIdentifierRecord(identifier, message, journalIdentifierDaoRepository);
				}
			}
		}
		List<SmpPublishServiceData> servicesForPublish = forPublish.getServiceList();
		for (SmpPublishServiceData serviceForPublish : servicesForPublish) {
			boolean isPublished = publishServiceMetadata(forPublish.getParticipantIdentifier(), serviceForPublish);
			if (!isPublished) {
				return false;
			} else {
				SmpDocumentIdentifier documentIdentifier = serviceForPublish.getDocumentIdentifier();
				String message = String.format("Service metadata with DocumentIdentifier value '%s' was published", documentIdentifier.getDocumentIdentifierValue());
				addJournalIdentifierRecord(identifier, message, journalIdentifierDaoRepository);
			}
		}
		return true;
	}

	private void addJournalIdentifierRecord(Identifier identifier, String message, JournalIdentifierDaoRepository journalIdentifierDaoRepository) {
		JournalIdentifier journalIdentifier = new JournalIdentifier();
		journalIdentifier.setIdentifier(identifier);
		journalIdentifier.setOrganisation(identifier.getOrganisation());
		journalIdentifier.setMessage(message);
		this.journalIdentifierDaoRepository.save(journalIdentifier);
	}

	private boolean isPublishDataValid(SmpPublishData publishData) {
		List<SmpPublishServiceData> serviceList = publishData.getServiceList();
		if(serviceList.isEmpty()) {
			log.info(String.format("Publish data for identifier '%s' has empty service list.", publishData.getParticipantIdentifier().toString()));
			return false;
		}
		for (SmpPublishServiceData serviceData : serviceList) {
			if(serviceData.getEndpoints().isEmpty()) {
				log.info(String.format("Service data for identifier '%s' has empty endpoint list.", publishData.getParticipantIdentifier().toString()));
				return false;
			}
		}
		return true;
	}

	private boolean contains(SmpPublishServiceData searchingService, List<SmpPublishServiceData> serviceList) {
		SmpDocumentIdentifier searchingDocumentIdentifier = searchingService.getDocumentIdentifier();
		String searchingDocumentIdentifierValue = searchingDocumentIdentifier.getDocumentIdentifierValue();
		for (SmpPublishServiceData serviceData : serviceList) {
			SmpDocumentIdentifier documentIdentifier = serviceData.getDocumentIdentifier();
			if(searchingDocumentIdentifierValue.equalsIgnoreCase(documentIdentifier.getDocumentIdentifierValue())) {
				return true;
			}
		}
		return false;
	}

	protected boolean deleteServiceGroup(ParticipantIdentifier participantIdentifier) {
		return smpIntegrationService.delete(createServiceGroupEndpointPath(participantIdentifier));
	}

	protected boolean deleteServiceMetadata(ParticipantIdentifier participantIdentifier, SmpPublishServiceData serviceData) {
		return smpIntegrationService.delete(createServiceMetadataEndpointPath(participantIdentifier, serviceData));
	}

	protected boolean publishServiceGroup(ParticipantIdentifier participantIdentifier, SmpPublishData smpPublishData) {
		String serviceGroupXml = smpXmlServiceFactory.createInstance().createServiceGroupXml(participantIdentifier);
		return smpIntegrationService.create(createServiceGroupEndpointPath(participantIdentifier), serviceGroupXml);

	}

	protected boolean publishServiceMetadata(ParticipantIdentifier participantIdentifier, SmpPublishServiceData serviceData) {
		String serviceMetadataXml = smpXmlServiceFactory.createInstance().createServiceMetadataXml(participantIdentifier, serviceData);
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
