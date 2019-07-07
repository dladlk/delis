package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.persistence.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SendDocumentRepository extends AbstractRepository<SendDocument> {

    List<SendDocument> findAllByOrganisationAndCreateTimeBetween(Organisation organisation, Date start, Date end);
    List<SendDocument> findAllByOrganisation(Organisation organisation);
    List<SendDocument> findAllByCreateTimeBetween(Date start, Date end);
}
