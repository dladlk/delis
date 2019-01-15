package dk.erst.delis.persistence.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Repository
public interface DocumentRepository extends AbstractRepository<Document> { }
