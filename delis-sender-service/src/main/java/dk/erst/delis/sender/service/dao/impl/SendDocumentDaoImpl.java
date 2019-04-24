package dk.erst.delis.sender.service.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.sender.service.dao.SendDocumentDao;

public class SendDocumentDaoImpl implements SendDocumentDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public int updateDocumentStatus(SendDocument document, SendDocumentStatus newStatus, SendDocumentStatus oldStatus) {
		Query q = entityManager.createQuery("update SendDocument set documentStatus = :newStatus where id = :id and documentStatus = :oldStatus");
		q.setParameter("newStatus", newStatus);
		q.setParameter("oldStatus", oldStatus);
		q.setParameter("id", document.getId());
		return q.executeUpdate();
	}

}
