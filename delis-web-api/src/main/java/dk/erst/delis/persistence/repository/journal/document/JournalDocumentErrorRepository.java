package dk.erst.delis.persistence.repository.journal.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalDocumentErrorRepository extends AbstractRepository<JournalDocumentError> {

    List<JournalDocumentError> findAllByJournalDocumentDocumentOrderById(Document journalDocumentDocument);
    Long countByJournalDocumentDocument(Document journalDocumentDocument);
}
