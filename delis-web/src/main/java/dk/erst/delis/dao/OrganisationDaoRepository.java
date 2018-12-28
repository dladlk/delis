package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Organisation;

public interface OrganisationDaoRepository extends PagingAndSortingRepository<Organisation, Long> {

	Organisation findByCode(String code);
	
}
