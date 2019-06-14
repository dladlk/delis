package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentBytesRepository extends AbstractRepository<DocumentBytes> {

    List<DocumentBytes> findByDocument(Document document);
    Long countByDocument(Document document);
    DocumentBytes findByIdAndDocumentId(Long id, Long documentId);
}
