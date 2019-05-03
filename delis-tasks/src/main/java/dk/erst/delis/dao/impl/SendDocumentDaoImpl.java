package dk.erst.delis.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.SendDocumentDao;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@Override
	public int updateLockedAndDocumentStatus(SendDocument document, SendDocumentStatus newStatus, SendDocumentStatus oldStatus, boolean lockOrUnlock) {
		Query q = entityManager.createQuery("update SendDocument set documentStatus = :newStatus, locked = :newLocked where id = :id and documentStatus = :oldStatus and locked = :oldLocked");
		q.setParameter("newStatus", newStatus);
		q.setParameter("oldStatus", oldStatus);
		q.setParameter("newLocked", lockOrUnlock);
		q.setParameter("oldLocked", !lockOrUnlock);
		q.setParameter("id", document.getId());
		int updated = q.executeUpdate();
		if (updated != 1) {
			log.warn("Unexpected result of updateLockedAndDocumentStatus (" + document.getId() + ", " + newStatus + ", " + oldStatus + ", " + lockOrUnlock + ") = " + updated);
		}
		return updated;
	}

}
