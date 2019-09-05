package dk.erst.delis.web.error;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;

@Repository
public interface ErrorDictionaryStatRepository extends org.springframework.data.repository.Repository<ErrorDictionary, Long> {

	@Query("select 'Total' " + ERROR_STAT_QUERY_MIDDLE + "1")
	ErrorDictionaryStat findErrorStatByErrorId(Long id);

	public static interface ErrorDictionaryStat {
		String getStatKey();

		long getDocumentCount();

		Date getMinCreateTime();

		Date getMaxCreateTime();
	}

	String ERROR_STAT_QUERY_MIDDLE = " as statKey, "

			+ "count(jde.journalDocument.document) as documentCount, "

			+ "max(jde.journalDocument.document.createTime) as maxCreateTime, "

			+ "min(jde.journalDocument.document.createTime) as minCreateTime "

			+ "from JournalDocumentError jde "

			+ "where jde.errorDictionary.id = ?1 "

			+ "group by ";

	String STAT_SENDER_NAME = "jde.journalDocument.document.senderName";
	String STAT_SENDER_COUNTRY = "jde.journalDocument.document.senderCountry";
	String STAT_DATE = "function('date_format', jde.journalDocument.document.createTime, '%Y-%m-%d')";
	String STAT_YEAR_MONTH = "function('date_format', jde.journalDocument.document.createTime, '%Y-%m')";

	@Query("select "

			+ STAT_SENDER_COUNTRY

			+ ERROR_STAT_QUERY_MIDDLE

			+ STAT_SENDER_COUNTRY)
	List<ErrorDictionaryStat> loadErrorStatBySenderCountry(Long id);

	@Query("select "

			+ STAT_SENDER_NAME

			+ ERROR_STAT_QUERY_MIDDLE

			+ STAT_SENDER_NAME)
	List<ErrorDictionaryStat> loadErrorStatBySenderName(Long id);

	@Query("select "

			+ STAT_DATE

			+ ERROR_STAT_QUERY_MIDDLE

			+ STAT_DATE)
	List<ErrorDictionaryStat> loadErrorStatByDate(Long id);

	@Query("select "

			+ STAT_YEAR_MONTH

			+ ERROR_STAT_QUERY_MIDDLE

			+ STAT_YEAR_MONTH)
	List<ErrorDictionaryStat> loadErrorStatByYearMonth(Long id);
}
