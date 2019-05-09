package dk.erst.delis.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import dk.erst.delis.data.entities.document.Document;
import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.DocumentDao;

public class DocumentDaoImpl implements DocumentDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void updateDocumentStatus(Document document) {
		Query q = entityManager.createQuery("update Document set documentStatus = :documentStatus, lastError = :lastError where id = :id");
		q.setParameter("documentStatus", document.getDocumentStatus());
		q.setParameter("lastError", document.getLastError());
		q.setParameter("id", document.getId());
		q.executeUpdate();
	}

}