package dk.erst.delis.dao;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;

@Transactional
public interface SendDocumentDao {

	int updateDocumentStatus(SendDocument document, SendDocumentStatus newStatus, SendDocumentStatus oldStatus);
	
	int updateLockedAndDocumentStatus(SendDocument document, SendDocumentStatus newStatus, SendDocumentStatus oldStatus, boolean lockOrUnlock);

	int markDocumentSent(SendDocument document, String messageId, Date deliveredDate);
	
}
