package dk.erst.delis.dao;

import dk.erst.delis.data.entities.journal.ErrorDictionary;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ErrorDictionaryDaoRepository extends PagingAndSortingRepository<ErrorDictionary, Long> {
	ErrorDictionary findFirstByCode(String code);
}
