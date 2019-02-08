package dk.erst.delis.dao;

import dk.erst.delis.data.entities.document.Document;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DocumentDao {

	void updateDocumentStatus(Document document);

//	void updateOutgoingRelativePath(Document document);

}
