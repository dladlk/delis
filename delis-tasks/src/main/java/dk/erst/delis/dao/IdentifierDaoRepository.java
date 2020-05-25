package dk.erst.delis.dao;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface IdentifierDaoRepository extends JpaRepository<Identifier, Long>, IdentifierDao {

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
	
	Identifier findByValueAndType(String value, String type);
	
	List<Identifier> findByOrganisation(Organisation organisation);
	
	@Query("select s from Identifier s where s.organisation = ?1 and s.id > ?2 order by s.id asc")
	List<Identifier> loadIdentifierList(Organisation organisation, long previousId, Pageable pageRequest);

	Long countByOrganisation(Organisation organisation);
	
	Long countByPublishingStatus(IdentifierPublishingStatus identifierPublishingStatus);
	
	Long countByPublishingStatusAndOrganisation(IdentifierPublishingStatus identifierPublishingStatus, Organisation organisaton);

	List<Identifier> findByPublishingStatus(IdentifierPublishingStatus identifierPublishingStatus);

}
