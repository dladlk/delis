package dk.erst.delis.dao;

import dk.erst.delis.data.entities.journal.JournalDocumentError;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JournalDocumentErrorDaoRepository extends PagingAndSortingRepository<JournalDocumentError, Long> {

}
