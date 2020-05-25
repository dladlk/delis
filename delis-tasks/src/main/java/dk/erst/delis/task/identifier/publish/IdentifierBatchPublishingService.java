package dk.erst.delis.task.identifier.publish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IdentifierBatchPublishingService {

    @Autowired
    private IdentifierDaoRepository identifierDaoRepository;

    @Autowired
    private IdentifierPublishService identifierPublishService;
	@Autowired
	private OrganisationSetupService organisationSetupService;
	

    public StatData publishPending(){
    	Map<Long, OrganisationSetupData> setupCache = new HashMap<Long, OrganisationSetupData>();
    	StatData statData = new StatData();
        List<Identifier> pendingIdentifiers = identifierDaoRepository.findByPublishingStatus(IdentifierPublishingStatus.PENDING);
        if (pendingIdentifiers != null && !pendingIdentifiers.isEmpty()) {
        	statData.increase("FOUND_PENDING", pendingIdentifiers.size());
        	performPublishing(identifierPublishService, pendingIdentifiers, statData, setupCache);
        }
        return statData;
    }

	private void performPublishing(IdentifierPublishService publishService, List<Identifier> pendingIdentifiers, StatData statData, Map<Long, OrganisationSetupData> setupCache) {
		for (Identifier pendingIdentifier : pendingIdentifiers) {
			OrganisationSetupData organisationSetupData = getOrganisationSetup(setupCache, pendingIdentifier.getOrganisation());

			IdentifierPublishingStatus newStatus = publishService.publishIdentifier(pendingIdentifier, organisationSetupData);

			pendingIdentifier.setPublishingStatus(newStatus);
			identifierDaoRepository.save(pendingIdentifier);
			if (newStatus == null || newStatus.isDone()) {
				statData.increment("DONE");
			} else {
				log.warn(String.format("Failed to publish identifier '%s' to SMP.", pendingIdentifier.getValue()));
				statData.increment("FAILED");
			}
		}
	}

	private OrganisationSetupData getOrganisationSetup(Map<Long, OrganisationSetupData> setupCache, Organisation organisation) {
		if (organisation != null) {
			OrganisationSetupData setupData = setupCache.get(organisation.getId());
			if (setupData == null) {
				setupData = organisationSetupService.load(organisation);
			}
			setupCache.put(organisation.getId(), setupData);
			return setupData;
		}
		return null;
	}
}
