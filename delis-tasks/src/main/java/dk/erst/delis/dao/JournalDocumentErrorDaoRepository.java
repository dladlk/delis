package dk.erst.delis.dao;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface JournalDocumentErrorDaoRepository extends PagingAndSortingRepository<JournalDocumentError, Long> {

	List<JournalDocumentError> findAllByJournalDocumentDocumentOrderById(Document journalDocumentDocument);

	@Query("select jde.journalDocument.document "
			+ "from JournalDocumentError jde fetch all properties where jde.errorDictionary.id = ?1"
	)
	List<Document> loadDocumentByErrorId(Long id);

	@Query("select count(jde.journalDocument.document) "
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	Integer getDocumentCountByErrorId(Long id);

	@Query("select max(jde.journalDocument.document.createTime) "
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	Date getDocumentMaxDate(Long id);

	@Query("select min(jde.journalDocument.document.createTime) "
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	Date getDocumentMinDate(Long id);
}
