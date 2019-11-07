package dk.erst.delis.task.identifier.publish;

import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.xml.SmpXmlServiceFactory;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class IdentifierPublishService {

	private final SmpXmlServiceFactory smpXmlServiceFactory;
	private final SmpIntegrationService smpIntegrationService;
	private final IdentifierPublishDataService identifierPublishDataService;
	private final SmpLookupService smpLookupService;
	@Autowired
	private JournalIdentifierDaoRepository journalIdentifierDaoRepository;

	@Autowired
	public IdentifierPublishService(SmpXmlServiceFactory smpXmlServiceFactory, SmpIntegrationService smpIntegrationService, IdentifierPublishDataService identifierPublishDataService,
			SmpLookupService smpLookupService) {
		this.smpXmlServiceFactory = smpXmlServiceFactory;
		this.smpIntegrationService = smpIntegrationService;
		this.identifierPublishDataService = identifierPublishDataService;
		this.smpLookupService = smpLookupService;
	}

	public IdentifierPublishingStatus publishIdentifier(Identifier identifier, OrganisationSetupData organisationSetupData) {
		long startTotal = System.currentTimeMillis();
		
		SmpPublishData forPublish = identifierPublishDataService.buildPublishData(identifier, organisationSetupData);
		SmpPublishData published = smpLookupService.lookup(forPublish.getParticipantIdentifier());
		if (identifier.getStatus().isDeleted() || !organisationSetupData.isSmpIntegrationPublish()) {
			IdentifierPublishingStatus deletionResultStatus = organisationSetupData.isSmpIntegrationPublish() ? IdentifierPublishingStatus.DONE : null; 
			if (published != null) {
				long startDelete = System.currentTimeMillis();
				boolean isDeleted = deleteServiceGroup(forPublish.getParticipantIdentifier());
				if (isDeleted) {
					addJournalIdentifierRecord(identifier, "Identifier is deleted from SMP", System.currentTimeMillis() - startDelete, journalIdentifierDaoRepository);
				}
				return deletionResultStatus;
			} else {
				addJournalIdentifierRecord(identifier, "Identifier is absent in SMP", System.currentTimeMillis() - startTotal, journalIdentifierDaoRepository);
				return deletionResultStatus;
			}
		}
		if (!isPublishDataValid(forPublish)) {
			log.info(String.format("Publish data created for identifier '%s' is invalid!", identifier.getValue()));
			return IdentifierPublishingStatus.FAILED;
		}
		
		long startPublishServiceGroup = System.currentTimeMillis();
		if (!publishServiceGroup(forPublish.getParticipantIdentifier(), forPublish)) {
			addJournalIdentifierRecord(identifier, "Failed to publish identifier", System.currentTimeMillis() - startPublishServiceGroup, journalIdentifierDaoRepository);
			return IdentifierPublishingStatus.FAILED;
		}
		
		int countDeleted = 0;
		int countFailed = 0;
		int countPublished = 0;
		
		if (published != null) {
			for (SmpPublishServiceData publishedService : published.getServiceList()) {
				if (!contains(publishedService, forPublish.getServiceList())) {
					long start = System.currentTimeMillis();
					boolean isDeleted = deleteServiceMetadata(published.getParticipantIdentifier(), publishedService);
					if (isDeleted) {
						SmpDocumentIdentifier documentIdentifier = publishedService.getDocumentIdentifier();
						String message = String.format("Deleted '%s'", documentIdentifier.getDocumentIdentifierValue());
						addJournalIdentifierRecord(identifier, message, System.currentTimeMillis() - start, journalIdentifierDaoRepository);
						countDeleted++;
					}
				}
			}
		}
		List<SmpPublishServiceData> servicesForPublish = forPublish.getServiceList();
		for (SmpPublishServiceData serviceForPublish : servicesForPublish) {
			long start = System.currentTimeMillis();
			boolean isPublished = publishServiceMetadata(forPublish.getParticipantIdentifier(), serviceForPublish);
			if (!isPublished) {
				String message = String.format("Failed to publish '%s'", serviceForPublish.getDocumentIdentifier());
				addJournalIdentifierRecord(identifier, message, System.currentTimeMillis() - startPublishServiceGroup, journalIdentifierDaoRepository);
				countFailed++;
			} else {
				SmpDocumentIdentifier documentIdentifier = serviceForPublish.getDocumentIdentifier();
				String message = String.format("Published '%s'", documentIdentifier.getDocumentIdentifierValue());
				addJournalIdentifierRecord(identifier, message, System.currentTimeMillis() - start, journalIdentifierDaoRepository);
				countPublished++;
			}
		}
		
		if (countDeleted + countFailed + countPublished > 0) {
			String message = String.format("Updated SMP registration with %d deleted, %d published and %d failed profiles", countDeleted, countPublished, countFailed);
			addJournalIdentifierRecord(identifier, message, System.currentTimeMillis() - startTotal, journalIdentifierDaoRepository);
		}
		
		return IdentifierPublishingStatus.DONE;
	}

	private void addJournalIdentifierRecord(Identifier identifier, String message, long durationMs, JournalIdentifierDaoRepository journalIdentifierDaoRepository) {
		JournalIdentifier journalIdentifier = new JournalIdentifier();
		journalIdentifier.setIdentifier(identifier);
		journalIdentifier.setOrganisation(identifier.getOrganisation());
		journalIdentifier.setMessage(message);
		journalIdentifier.setDurationMs(durationMs);
		this.journalIdentifierDaoRepository.save(journalIdentifier);
	}

	private boolean isPublishDataValid(SmpPublishData publishData) {
		List<SmpPublishServiceData> serviceList = publishData.getServiceList();
		if (serviceList.isEmpty()) {
			log.info(String.format("Publish data for identifier '%s' has empty service list.", publishData.getParticipantIdentifier().toString()));
			return false;
		}
		for (SmpPublishServiceData serviceData : serviceList) {
			if (serviceData.getEndpoints().isEmpty()) {
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
			if (searchingDocumentIdentifierValue.equalsIgnoreCase(documentIdentifier.getDocumentIdentifierValue())) {
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
