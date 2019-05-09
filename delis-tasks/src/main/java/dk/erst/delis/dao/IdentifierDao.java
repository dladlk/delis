package dk.erst.delis.dao;

import dk.erst.delis.data.entities.identifier.Identifier;

import java.util.List;

public interface IdentifierDao {

	List<Identifier> getPendingForDeactivation(long organisationId, long lastSyncFactId);
}
