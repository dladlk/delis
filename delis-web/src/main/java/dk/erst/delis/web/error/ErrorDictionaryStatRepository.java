package dk.erst.delis.web.error;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;

@Repository
public interface ErrorDictionaryStatRepository extends org.springframework.data.repository.Repository<ErrorDictionary, Long> {

	@Query("select "
			+ "'Total' as statKey, "
			+ "count(jde.journalDocument.document) as documentCount, "
			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "
			+ "min(jde.journalDocument.document.createTime) as minCreateTime "
			+ "from JournalDocumentError jde where jde.errorDictionary.id = ?1"
	)
	ErrorDictionaryStat findErrorStatByErrorId(Long id);
	
	public static interface ErrorDictionaryStat {
		String getStatKey();
		long getDocumentCount();
		Date getMinCreateTime();
		Date getMaxCreateTime();
	}
	
	public static enum ErrorDictionaryStatType {
		DATE,
		
		YEAR_MONTH,
		
		SUPPLIER,
		
		SUPPLIER_COUNTRY
	}
	
	@Query("select "
			+ "jde.journalDocument.document.senderCountry as statKey, "
			+ "count(jde.journalDocument.document) as documentCount, "
			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "
			+ "min(jde.journalDocument.document.createTime) as minCreateTime "
			+ "from JournalDocumentError jde "
			+ "where jde.errorDictionary.id = ?1 "
			+ "group by jde.journalDocument.document.senderCountry"
	)
	List<ErrorDictionaryStat> loadErrorStatBySenderCountry(Long id);
	
	
	@Query("select "
			+ "jde.journalDocument.document.senderName as statKey, "
			+ "count(jde.journalDocument.document) as documentCount, "
			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "
			+ "min(jde.journalDocument.document.createTime) as minCreateTime "
			+ "from JournalDocumentError jde "
			+ "where jde.errorDictionary.id = ?1 "
			+ "group by jde.journalDocument.document.senderName"
	)
	List<ErrorDictionaryStat> loadErrorStatBySenderName(Long id);	
}
