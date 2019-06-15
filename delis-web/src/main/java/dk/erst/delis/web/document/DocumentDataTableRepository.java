package dk.erst.delis.web.document;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.document.Document;

@Repository
public interface DocumentDataTableRepository extends DataTablesRepository<Document, Long> {
}
