package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends AbstractRepository<Document> {

    Long countByCreateTimeBetweenAndOrganisationAndLastErrorIsNull(Date start, Date end, Organisation organisation);
    Long countByCreateTimeBetweenAndOrganisationAndLastErrorIsNotNull(Date start, Date end, Organisation organisation);
    Long countByCreateTimeBetweenAndOrganisation(Date start, Date end, Organisation organisation);

    Long countByCreateTimeBetweenAndLastErrorIsNull(Date start, Date end);
    Long countByCreateTimeBetweenAndLastErrorIsNotNull(Date start, Date end);

    List<Document> findAllByOrganisationAndCreateTimeBetweenAndLastErrorIsNull(Organisation organisation, Date start, Date end);
    List<Document> findAllByOrganisationAndLastErrorIsNull(Organisation organisation);
    List<Document> findAllByCreateTimeBetweenAndLastErrorIsNull(Date start, Date end);
    List<Document> findAllByLastErrorIsNull();
    List<Document> findAllByOrganisationAndCreateTimeBetweenAndLastErrorIsNotNull(Organisation organisation, Date start, Date end);
    List<Document> findAllByOrganisationAndLastErrorIsNotNull(Organisation organisation);
    List<Document> findAllByCreateTimeBetweenAndLastErrorIsNotNull(Date start, Date end);
    List<Document> findAllByLastErrorIsNotNull();
}
