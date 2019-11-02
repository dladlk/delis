package dk.erst.delis.web.error;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;

@Repository
public interface ErrorDictionaryDataTableRepository extends DataTablesRepository<ErrorDictionary, Long> {
}
