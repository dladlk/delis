package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentErrorCode;

public interface ErrorDictionaryDaoRepository extends PagingAndSortingRepository<ErrorDictionary, Long> {
	
	ErrorDictionary findFirstByCode(String code);
	
	ErrorDictionary findFirstByErrorTypeAndMessage(DocumentErrorCode errorType, String message);

	List<ErrorDictionary> findAllByHashOrderByIdAsc(int hash);
	
	@Query("select s "
			+ "from ErrorDictionary s "
			+ "where s.id > ?1 "
			+ "order by s.id "
	)	
	List<ErrorDictionary> loadListForReorg(long id, Pageable page);

	@Query("select s.hash, min(s.id) as minId "
			+ "from ErrorDictionary s "
			+ "group by s.hash "
			+ "having count(*) > 1 "
	)	
	List<Object[]> findDuplicatedByHash();
}
