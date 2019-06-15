package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentErrorCode;

public interface ErrorDictionaryDaoRepository extends PagingAndSortingRepository<ErrorDictionary, Long> {
	
	ErrorDictionary findFirstByCode(String code);
	
	ErrorDictionary findFirstByErrorTypeAndMessage(DocumentErrorCode errorType, String message);

	List<ErrorDictionary> findAllByHash(int hash);
}
