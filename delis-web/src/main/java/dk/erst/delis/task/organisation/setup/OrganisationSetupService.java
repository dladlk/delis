package dk.erst.delis.task.organisation.setup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.OrganisationSetupDaoRepository;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.OrganisationSetup;
import dk.erst.delis.data.OrganisationSetupKey;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrganisationSetupService {

	private OrganisationSetupDaoRepository organisationSetupDaoRepository;

	@Autowired
	public OrganisationSetupService(OrganisationSetupDaoRepository organisationSetupDaoRepository) {
		this.organisationSetupDaoRepository = organisationSetupDaoRepository;
	}

	public OrganisationSetupData load(Organisation organisation) {
		if (organisation == null) {
			return null;
		}
		List<OrganisationSetup> setupList = organisationSetupDaoRepository.findAllByOrganisation(organisation);
		return convertSetupListToData(setupList, organisation);
	}

	public StatData update(OrganisationSetupData data) {
		if (data == null) {
			return null;
		}
		StatData sd = new StatData();
		Map<OrganisationSetupKey, String> newDataToSetupMap = convertDataToSetupMap(data);
		List<OrganisationSetup> currentSetupList = organisationSetupDaoRepository.findAllByOrganisation(data.getOrganisation());
		Map<OrganisationSetupKey, OrganisationSetup> currentSetupMap = currentSetupList.stream().collect(Collectors.toMap(OrganisationSetup::getKey, Function.identity()));

		if (log.isDebugEnabled()) {
			log.debug("Build newDataMap: " + newDataToSetupMap);
			log.debug("Current detup map: " + currentSetupMap);
		}

		for (OrganisationSetupKey key : newDataToSetupMap.keySet()) {
			String newValue = newDataToSetupMap.get(key);
			if (currentSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				if (!newValue.equals(os.getValue())) {
					os.setValue(newValue);
					organisationSetupDaoRepository.save(os);
					sd.increment("updated");
				} else {
					sd.increment("unchanged");
				}
			} else {
				OrganisationSetup os = new OrganisationSetup();
				os.setKey(key);
				os.setOrganisation(data.getOrganisation());
				os.setValue(newValue);
				organisationSetupDaoRepository.save(os);
				sd.increment("created");
			}
		}

		for (OrganisationSetupKey key : currentSetupMap.keySet()) {
			if (!newDataToSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				organisationSetupDaoRepository.delete(os);
				sd.increment("deleted");
			}
		}

		return sd;
	}

/*	private Map<OrganisationSetupKey, String> buildDefaultMap() {
		Map<OrganisationSetupKey, String> m = new HashMap<>();
		m.put(OrganisationSetupKey.RECEIVING_DATA_FORMAT, OrganisationReceivingFormatRule.OIOUBL.getCode());
		m.put(OrganisationSetupKey.RECEIVING_METHOD, OrganisationReceivingMethod.FILE_SYSTEM.getCode());
		m.put(OrganisationSetupKey.RECEIVING_METHOD_SETUP, "/delis/output");
		m.put(OrganisationSetupKey.SUBSCRIBED_SMP_PROFILES, OrganisationSubscriptionProfileGroup.BIS3.getCode());
		return m;
	}*/

	protected Map<OrganisationSetupKey, String> convertDataToSetupMap(OrganisationSetupData d) {
		Map<OrganisationSetupKey, String> m = new HashMap<>();
		if (d == null) {
			return m;
		}

		if (d.getReceivingFormatRule() != null) {
			m.put(OrganisationSetupKey.RECEIVING_DATA_FORMAT, d.getReceivingFormatRule().getCode());
		}
		if (d.getReceivingMethod() != null) {
			m.put(OrganisationSetupKey.RECEIVING_METHOD, d.getReceivingMethod().getCode());
		}
		if (d.getReceivingMethodSetup() != null) {
			m.put(OrganisationSetupKey.RECEIVING_METHOD_SETUP, d.getReceivingMethodSetup().trim());
		}
		if (d.getSubscribeProfileSet() != null) {
			String joined = d.getSubscribeProfileSet().stream().map(OrganisationSubscriptionProfileGroup::getCode).collect(Collectors.joining(","));
			m.put(OrganisationSetupKey.SUBSCRIBED_SMP_PROFILES, joined);
		}
		return m;
	}

	protected OrganisationSetupData convertSetupListToData(List<OrganisationSetup> setupList, Organisation organisation) {
		OrganisationSetupData d = new OrganisationSetupData();
		d.setOrganisation(organisation);
		if (setupList != null) {
			for (OrganisationSetup os : setupList) {
				switch (os.getKey()) {
				case SUBSCRIBED_SMP_PROFILES:
					String[] profiles = os.getValue().split(",");

					Set<OrganisationSubscriptionProfileGroup> set = new HashSet<>();
					for (int i = 0; i < profiles.length; i++) {
						if (!profiles[i].isEmpty()) {
							OrganisationSubscriptionProfileGroup profileGroup = OrganisationSubscriptionProfileGroup.valueOf(profiles[i]);
							if (profileGroup != null) {
								set.add(profileGroup);
							}
						}
					}
					d.setSubscribeProfileSet(set);
					break;
				case RECEIVING_DATA_FORMAT:
					d.setReceivingFormatRule(OrganisationReceivingFormatRule.valueOf(os.getValue()));
					break;
				case RECEIVING_METHOD:
					d.setReceivingMethod(OrganisationReceivingMethod.valueOf(os.getValue()));
					break;
				case RECEIVING_METHOD_SETUP:
					d.setReceivingMethodSetup(os.getValue());
					break;
				}
			}
		}
		if (d.getSubscribeProfileSet() == null) {
			d.setSubscribeProfileSet(Collections.emptySet());
		}
		return d;
	}
}
