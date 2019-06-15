package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;

public interface SendDocumentDaoRepository extends PagingAndSortingRepository<SendDocument, Long>, SendDocumentDao, QueryByExampleExecutor<SendDocument> {
	
	SendDocument findTop1ByDocumentStatusOrderByIdAsc(SendDocumentStatus documentStatus);

	SendDocument findTop1ByDocumentStatusAndDocumentTypeAndOrganisationOrderByIdAsc(SendDocumentStatus documentStatus, DocumentType documentType, Organisation organisation);

}
