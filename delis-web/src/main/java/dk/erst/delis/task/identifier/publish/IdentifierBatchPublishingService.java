package dk.erst.delis.task.identifier.publish;

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

    public List<Long> publishPending(){
        List<Identifier> pendingIdentifiers = identifierDaoRepository.findByPublishingStatus(IdentifierPublishingStatus.PENDING);
        List<Identifier> publishedIdentifiers = performPublishing(identifierPublishService, pendingIdentifiers);
        List<Long> publishedIds = new ArrayList<>();
        publishedIdentifiers.forEach(identifier -> publishedIds.add(identifier.getId()));
        return publishedIds;
    }

    private List<Identifier> performPublishing(IdentifierPublishService publishService, List<Identifier> pendingIdentifiers) {
        List<Identifier> publishedIdentifiers = new ArrayList<>();
        for (Identifier pendingIdentifier : pendingIdentifiers) {
            if (publishService.publishIdentifier(pendingIdentifier)) {
                pendingIdentifier.setPublishingStatus(IdentifierPublishingStatus.DONE);
                publishedIdentifiers.add(pendingIdentifier);
                identifierDaoRepository.save(pendingIdentifier);
                log.debug(String.format("Identifier '%s' successfully published to SMP.", pendingIdentifier.getValue()));
            } else {
                log.warn(String.format("Failed to publish identifier '%s' to SMP.", pendingIdentifier.getValue()));
            }
        }
        return publishedIdentifiers;
    }
}
