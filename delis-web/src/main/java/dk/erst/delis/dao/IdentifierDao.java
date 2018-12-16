package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.Identifier;

public interface IdentifierDao {

	List<Identifier> getPendingForDeactivation(long organisationId, long lastSyncFactId);
}
