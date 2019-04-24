package dk.erst.delis.sender.service.dao;

import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;

@Transactional
public interface SendDocumentDao {

	int updateDocumentStatus(SendDocument document, SendDocumentStatus newStatus, SendDocumentStatus oldStatus);
	
}
