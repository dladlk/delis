package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocumentError;

public interface JournalDocumentErrorDaoRepository extends PagingAndSortingRepository<JournalDocumentError, Long> {

	List<JournalDocumentError> findAllByJournalDocumentDocumentOrderById(Document journalDocumentDocument);

	@Query("select jde.journalDocument.document "
			+ "from JournalDocumentError jde fetch all properties where jde.errorDictionary.id = ?1"
	)
	List<Document> loadDocumentByErrorId(Long id);

	@Transactional
	@Modifying
	@Query(value = "update journal_document_error set error_dictionary_id_pk = ?2 where error_dictionary_id_pk = ?1", nativeQuery = true)
	int updateErrorDictionaryId(long oldErrorDictionaryId, long newErrorDictionaryId);

}
