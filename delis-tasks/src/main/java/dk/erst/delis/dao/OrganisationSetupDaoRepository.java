package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;

public interface OrganisationSetupDaoRepository extends PagingAndSortingRepository<OrganisationSetup, Long>, QueryByExampleExecutor<OrganisationSetup> {

	List<OrganisationSetup> findAllByOrganisation(Organisation organisation);

	List<OrganisationSetup> findAllByKeyAndValue(OrganisationSetupKey key, String value);
	
}
