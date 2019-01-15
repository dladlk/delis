package dk.erst.delis.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.IdentifierDao;

public class IdentifierDaoImpl implements IdentifierDao {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public List<Identifier> getPendingForDeactivation(long organisationId, long lastSyncFactId) {
		TypedQuery<Identifier> q = entityManager.createQuery("select i from Identifier i where i.lastSyncOrganisationFactId != :lastSyncFactId and i.status = :oldStatus and i.organisation.id = :organisationId ", Identifier.class);
		q.setParameter("organisationId", organisationId);
		q.setParameter("oldStatus", IdentifierStatus.ACTIVE);
		q.setParameter("lastSyncFactId", lastSyncFactId);
		return q.getResultList();
	}

}
