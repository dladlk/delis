package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.persistence.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SendDocumentRepository extends AbstractRepository<SendDocument> {

    Long countByCreateTimeBetweenAndOrganisationId(Date start, Date end, Long organisationId);
}
