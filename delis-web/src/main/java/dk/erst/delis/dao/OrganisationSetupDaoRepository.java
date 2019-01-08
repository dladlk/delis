package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.OrganisationSetup;

public interface OrganisationSetupDaoRepository extends PagingAndSortingRepository<OrganisationSetup, Long> {

	List<OrganisationSetup> findAllByOrganisation(Organisation organisation);
	
}
