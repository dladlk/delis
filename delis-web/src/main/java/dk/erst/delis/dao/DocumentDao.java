package dk.erst.delis.dao;

import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.Document;

@Transactional
public interface DocumentDao {

	void updateDocumentStatus(Document document);

	void updateOutgoingRelativePath(Document document);

}
