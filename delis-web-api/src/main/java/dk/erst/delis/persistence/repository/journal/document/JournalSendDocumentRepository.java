package dk.erst.delis.persistence.repository.journal.document;

import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.persistence.AbstractRepository;

import java.util.List;

public interface JournalSendDocumentRepository extends AbstractRepository<JournalSendDocument> {

    Long countByDocumentId(Long sendDocumentId);

    List<JournalSendDocument> findByDocumentId(Long sendDocumentId);
}
