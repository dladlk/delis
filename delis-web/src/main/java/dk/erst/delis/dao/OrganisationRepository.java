package dk.erst.delis.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Organisation;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {

	@Query("select s.organisation.id as organisationId, count(*) as identifierCount from Identifier s group by s.organisation.id")
	List<Map<?,?>> loadIndetifierStat();
	
}
