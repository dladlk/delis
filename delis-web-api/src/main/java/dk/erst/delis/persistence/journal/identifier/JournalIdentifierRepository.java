package dk.erst.delis.persistence.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

/**
 * @author funtusthan, created by 13.01.19
 */

@Repository
public interface JournalIdentifierRepository extends AbstractRepository<JournalIdentifier> { }
