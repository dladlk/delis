package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocumentError;

public interface JournalDocumentErrorDaoRepository extends PagingAndSortingRepository<JournalDocumentError, Long> {

	List<JournalDocumentError> findAllByJournalDocumentDocumentOrderById(Document journalDocumentDocument);

	@Query("select jde.journalDocument.document "
			+ "from JournalDocumentError jde fetch all properties where jde.errorDictionary.id = ?1"
	)
	List<Document> loadDocumentByErrorId(Long id);

}
