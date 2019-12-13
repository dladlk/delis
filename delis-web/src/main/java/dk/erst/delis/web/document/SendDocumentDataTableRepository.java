package dk.erst.delis.web.document;

import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;

@Repository
public interface SendDocumentDataTableRepository extends DataTablesRepository<SendDocument, Long> {

}
