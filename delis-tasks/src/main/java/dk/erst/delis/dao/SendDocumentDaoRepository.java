package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;

public interface SendDocumentDaoRepository extends PagingAndSortingRepository<SendDocument, Long> {
	
	List<SendDocument> findTop1ByDocumentStatusOrderByIdAsc(SendDocumentStatus documentStatus);

}
