package dk.erst.delis.web.error;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;

@Repository
public interface ErrorDictionaryStatRepository extends org.springframework.data.repository.Repository<ErrorDictionary, Long> {

	@Query("select "
			+ "count(jde.journalDocument.document) as documentCount, "
			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "
			+ "min(jde.journalDocument.document.createTime) as minCreateTime "
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	ErrorDictionaryStat findErrorStatByErrorId(Long id);
	
	public static interface ErrorDictionaryStat {
		long getDocumentCount();
		Date getMinCreateTime();
		Date getMaxCreateTime();
	}
}
