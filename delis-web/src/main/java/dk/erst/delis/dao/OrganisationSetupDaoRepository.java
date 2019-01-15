package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganisationSetupDaoRepository extends PagingAndSortingRepository<OrganisationSetup, Long> {

	List<OrganisationSetup> findAllByOrganisation(Organisation organisation);
	
}
