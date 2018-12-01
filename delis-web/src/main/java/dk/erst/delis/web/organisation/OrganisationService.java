package dk.erst.delis.web.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.Organisation;

@Service
public class OrganisationService {

	private OrganisationRepository organisationRepository;

	@Autowired
	public OrganisationService(OrganisationRepository organisationRepository) {
		this.organisationRepository = organisationRepository;
	}

	public Iterable<Organisation> getOrganisations() {
		return organisationRepository.findAll(Sort.by("name"));
	}

	public void saveOrganisation(Organisation organisation) {
		this.organisationRepository.save(organisation);
	}

}
