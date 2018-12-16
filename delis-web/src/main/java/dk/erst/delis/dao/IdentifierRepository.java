package dk.erst.delis.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.IdentifierPublishingStatus;
import dk.erst.delis.data.IdentifierStatus;
import dk.erst.delis.data.Organisation;

public interface IdentifierRepository extends JpaRepository<Identifier, Long> {

	@Query("select s.organisation.id as organisationId, s.status as status, s.publishingStatus as publishingStatus, count(*) as identifierCount "
			+ "from Identifier s "
			+ "group by s.organisation.id, s.status, s.publishingStatus "
			+ "order by s.organisation.id, s.status, s.publishingStatus") 
	List<Map<String, Object>> loadIndetifierStat();
	
	@Query("select s.organisation.id as organisationId, s.status as status, s.publishingStatus as publishingStatus, count(*) as identifierCount "
			+ "from Identifier s "
			+ "where s.organisation.id = ?1 "
			+ "group by s.organisation.id, s.status, s.publishingStatus "
			) 
	List<Map<String, Object>> loadIndetifierStatByOrganisation(long organisationId);
	
	Identifier findByOrganisationAndValueAndType(Organisation organisation, String value, String type);
	
	List<Identifier> findByOrganisation(Organisation organisation);

	Long countByOrganisation(Organisation organisation);
	
	Long countByPublishingStatus(IdentifierPublishingStatus identifierPublishingStatus);

	@Query("select i from Identifier i "
			+ "where i.lastSyncOrganisationFactId != :lastSyncFactId and i.status = :oldStatus and i.organisation.id = :organisationId  ") 	
	Iterator<Identifier> getPendingForDeactivation(
			@Param("organisationId") long organisationId, 
			@Param("lastSyncFactId") long lastSyncFactId, 
			@Param("oldStatus") IdentifierStatus oldStatus);

}
