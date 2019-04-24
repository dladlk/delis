package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.pagefiltering.persistence.AbstractRepository;

public interface SendDocumentDaoRepository extends AbstractRepository<SendDocument> {
	
	List<SendDocument> findTop1ByDocumentStatusOrderByIdAsc(SendDocumentStatus documentStatus);

}
