package dk.erst.delis.web.document;

import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;

@Repository
public interface DocumentDataTableRepository extends DataTablesRepository<Document, Long> {

}
