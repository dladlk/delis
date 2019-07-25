package dk.erst.delis.persistence.repository.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IdentifierRepository extends AbstractRepository<Identifier> {

    List<Identifier> findByPublishingStatusAndCreateTimeBetween(IdentifierPublishingStatus publishingStatus, Date start, Date End);
}
