package dk.erst.delis.web.error;

import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;

@Repository
public interface ErrorDictionaryDataTableRepository extends DataTablesRepository<ErrorDictionary, Long> {
}
