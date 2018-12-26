package dk.erst.delis.dao;

import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.Document;

public interface DocumentDao {

	@Transactional
	void updateDocumentStatus(Document document);
	
}
