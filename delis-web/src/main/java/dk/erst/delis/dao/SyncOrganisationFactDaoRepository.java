package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface SyncOrganisationFactDaoRepository extends PagingAndSortingRepository<SyncOrganisationFact, Long> {

	public List<SyncOrganisationFact> findTop5ByOrganisationOrderByIdDesc(Organisation organisation);
	
}
