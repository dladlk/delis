package dk.erst.delis.dao;

import dk.erst.delis.data.entities.document.SendDocument;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SendDocumentDataTableRepository extends DataTablesRepository<SendDocument, Long> {
}
