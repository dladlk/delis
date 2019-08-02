package dk.erst.delis.persistence.repository.journal.document;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.persistence.AbstractRepository;

import java.util.List;

@Repository
public interface JournalDocumentRepository extends AbstractRepository<JournalDocument> {

	List<JournalDocument> findAllByDocumentId(long documentId, Sort sort);

	Long countByDocumentId(long documentId);
}
