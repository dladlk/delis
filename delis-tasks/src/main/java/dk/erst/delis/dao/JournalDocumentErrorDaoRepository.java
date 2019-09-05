package dk.erst.delis.dao;

import java.util.Date;
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

	@Query("select "
//			+ "new dk.erst.delis.dao.ErrorDictionaryStat("
			+ "count(jde.journalDocument.document) as documentCount, "
			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "
			+ "min(jde.journalDocument.document.createTime) as minCreateTime "
//			+ ")"
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	ErrorDictionaryStat findErrorStatByErrorId(Long id);
	
	public static interface ErrorDictionaryStat {
		long getDocumentCount();
		Date getMinCreateTime();
		Date getMaxCreateTime();
	}

}
