package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author funtusthan, created by 25.02.19
 */

@Repository
public interface DocumentBytesRepository extends AbstractRepository<DocumentBytes> {

    List<DocumentBytes> findByDocument(Document document);
    Long countByDocument(Document document);
}
