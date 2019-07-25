package dk.erst.delis.persistence.repository.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalIdentifierRepository extends AbstractRepository<JournalIdentifier> {

    List<JournalIdentifier> findAllByIdentifierId(long identifierId, Sort sort);
    Long countByIdentifierId(long identifierId);
}
