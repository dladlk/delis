package dk.erst.delis.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.SendDocumentDao;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;

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

	@Override
	public int markDocumentSent(SendDocument document, String messageId, Date deliveredDate) {
		Query q = entityManager.createQuery("update SendDocument set documentStatus = :newStatus, sentMessageId = :messageId, deliveredTime = :deliveredTime where id = :id");
		q.setParameter("newStatus", SendDocumentStatus.SEND_OK);
		q.setParameter("messageId", messageId);
		q.setParameter("deliveredTime", deliveredDate);
		q.setParameter("id", document.getId());
		return q.executeUpdate();
	}

}