package dk.erst.delis.dao;

import dk.erst.delis.data.entities.organisation.Organisation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganisationDaoRepository extends PagingAndSortingRepository<Organisation, Long> {

	Organisation findByCode(String code);

	Organisation findTop1ByName(String name);
	
}
