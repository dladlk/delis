package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;

public interface OrganisationSetupDaoRepository extends PagingAndSortingRepository<OrganisationSetup, Long>, QueryByExampleExecutor<OrganisationSetup> {

	List<OrganisationSetup> findAllByOrganisation(Organisation organisation);
	
}
