package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocumentError;

public interface JournalDocumentErrorDaoRepository extends PagingAndSortingRepository<JournalDocumentError, Long> {

	public List<JournalDocumentError> findAllByJournalDocumentDocumentOrderById(Document journalDocumentDocument);
}
