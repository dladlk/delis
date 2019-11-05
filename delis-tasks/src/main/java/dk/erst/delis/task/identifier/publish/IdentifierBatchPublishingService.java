package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.IdentifierDaoRepository;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IdentifierBatchPublishingService {

    @Autowired
    private IdentifierDaoRepository identifierDaoRepository;

    @Autowired
    private IdentifierPublishService identifierPublishService;

    public StatData publishPending(){
    	StatData statData = new StatData();
        List<Identifier> pendingIdentifiers = identifierDaoRepository.findByPublishingStatus(IdentifierPublishingStatus.PENDING);
        if (pendingIdentifiers != null && !pendingIdentifiers.isEmpty()) {
        	statData.increase("FOUND_PENDING", pendingIdentifiers.size());
        }
        performPublishing(identifierPublishService, pendingIdentifiers, statData);
        return statData;
    }

    private List<Identifier> performPublishing(IdentifierPublishService publishService, List<Identifier> pendingIdentifiers, StatData statData) {
        List<Identifier> publishedIdentifiers = new ArrayList<>();
        for (Identifier pendingIdentifier : pendingIdentifiers) {
            if (publishService.publishIdentifier(pendingIdentifier)) {
                pendingIdentifier.setPublishingStatus(IdentifierPublishingStatus.DONE);
                publishedIdentifiers.add(pendingIdentifier);
                identifierDaoRepository.save(pendingIdentifier);
                log.debug(String.format("Identifier '%s' successfully published to SMP.", pendingIdentifier.getValue()));
                statData.increment("DONE");
            } else {
                log.warn(String.format("Failed to publish identifier '%s' to SMP.", pendingIdentifier.getValue()));
                statData.increment("FAILED");
            }
        }
        return publishedIdentifiers;
    }
}
