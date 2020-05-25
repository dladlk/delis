package dk.erst.delis.task.organisation;

import dk.erst.delis.data.entities.organisation.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.OrganisationDaoRepository;

@Service
public class OrganisationService {

	private OrganisationDaoRepository organisationDaoRepository;

	@Autowired
	public OrganisationService(OrganisationDaoRepository organisationDaoRepository) {
		this.organisationDaoRepository = organisationDaoRepository;
	}

	public Iterable<Organisation> getOrganisations() {
		return organisationDaoRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase()));
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
	public Organisation findOrganisationByName(String name) {
		return this.organisationDaoRepository.findTop1ByName(name);
	}
}
