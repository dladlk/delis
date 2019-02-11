package dk.erst.delis.persistence.repository.journal.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.persistence.AbstractRepository;

/**
 * @author funtusthan, created by 13.01.19
 */

@Repository
public interface JournalDocumentRepository extends AbstractRepository<JournalDocument> {

	Page<JournalDocument> findAllByDocumentId(long documentId, Pageable pageable);

	Long countByDocumentId(long documentId);
}
