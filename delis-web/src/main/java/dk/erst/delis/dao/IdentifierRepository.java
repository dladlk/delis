package dk.erst.delis.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.Organisation;

public interface IdentifierRepository extends PagingAndSortingRepository<Identifier, Long> {

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

}
