package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends AbstractRepository<Document> {

    Long countByLastErrorNotNullAndCreateTimeBetween(Date start, Date end);
    Long countByDocumentStatusInAndCreateTimeBetween(List<DocumentStatus> statuses, Date start, Date end);
    Long countByCreateTimeBetweenAndOrganisationId(Date start, Date end, Long organisationId);
    Long countByCreateTimeBetweenAndOrganisationIdAndDocumentStatusIn(Date start, Date end, Long organisationId, List<DocumentStatus> documentStatuses);
    Long countByCreateTimeBetweenAndDocumentStatusIn(Date start, Date end, List<DocumentStatus> documentStatuses);

    List<Document> findByCreateTimeBetween(Date start, Date end);
    List<Document> findByIdIn(List<Long> longs);
}
