package dk.erst.delis.web.organisation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.OrganisationRepository;
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

	public Organisation findOrganisation(long id) {
		return this.organisationRepository.findById(id).get();
	}

	public Organisation findOrganisationByCode(String code) {
		return this.organisationRepository.findByCode(code);
	}

	public OrganisationData loadOrganisationData(long organisationId) {
		Organisation o = findOrganisation(organisationId);
		if (o != null) {
			organisationRepository.count();
		}
		return OrganisationData.builder().organisation(o).identifierCount(1).documentCount(10).build();
	}

	public Map<Long,Long> loadOrganisationIdentifierStatMap() {
		List<Map<?, ?>> list = this.organisationRepository.loadIndetifierStat();
		Map<Long, Long> mapRes = new HashMap<>();
		for (Map<?, ?> map : list) {
			Long identifierCount = (Long) map.get("identifierCount");
			Long organisationId = (Long) map.get("organisationId");
			mapRes.put(organisationId, identifierCount);
		}
		return mapRes;
	}

}
