package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.data.entities.organisation.Organisation;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface JournalOrganisationDaoRepository extends PagingAndSortingRepository<JournalOrganisation, Long> {

	List<JournalOrganisation> findTop5ByOrganisationOrderByIdDesc(Organisation organisation);
}
