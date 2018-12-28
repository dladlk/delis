package dk.erst.delis.web.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.Organisation;

@Service
public class OrganisationService {

	private OrganisationDaoRepository organisationDaoRepository;

	@Autowired
	public OrganisationService(OrganisationDaoRepository organisationDaoRepository) {
		this.organisationDaoRepository = organisationDaoRepository;
	}

	public Iterable<Organisation> getOrganisations() {
		return organisationDaoRepository.findAll(Sort.by("name"));
	}

	public void saveOrganisation(Organisation organisation) {
		this.organisationDaoRepository.save(organisation);
	}

	public Organisation findOrganisation(long id) {
		return this.organisationDaoRepository.findById(id).get();
	}

	public Organisation findOrganisationByCode(String code) {
		return this.organisationDaoRepository.findByCode(code);
	}
}
