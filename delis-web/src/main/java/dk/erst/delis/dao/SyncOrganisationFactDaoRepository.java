package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.SyncOrganisationFact;

public interface SyncOrganisationFactDaoRepository extends PagingAndSortingRepository<SyncOrganisationFact, Long> {

	public List<SyncOrganisationFact> findTop5ByOrganisationOrderByIdDesc(Organisation organisation);
	
}
