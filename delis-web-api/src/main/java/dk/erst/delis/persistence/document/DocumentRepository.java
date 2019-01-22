package dk.erst.delis.persistence.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Repository
public interface DocumentRepository extends AbstractRepository<Document> {

    Long countByReceiverIdentifierIn(List<Identifier> identifiers);
    Long countByLastErrorNotNull();
    Long countByDocumentStatusIn(List<DocumentStatus> statuses);
    Long countByCreateTimeBetween(Date start, Date end);
}
