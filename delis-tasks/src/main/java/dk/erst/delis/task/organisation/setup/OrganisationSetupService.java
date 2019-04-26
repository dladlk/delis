package dk.erst.delis.task.organisation.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.OrganisationSetupDaoRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
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
		List<OrganisationSetupKey> changedFields = new ArrayList<>();
		for (OrganisationSetupKey key : newDataToSetupMap.keySet()) {
			String newValue = newDataToSetupMap.get(key);
			if (currentSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				if (!newValue.equals(os.getValue())) {
					os.setValue(newValue);
					organisationSetupDaoRepository.save(os);
					sd.increment("updated");
					changedFields.add(key);
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
				changedFields.add(key);
			}
		}

		for (OrganisationSetupKey key : currentSetupMap.keySet()) {
			if (!newDataToSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				organisationSetupDaoRepository.delete(os);
				sd.increment("deleted");
				changedFields.add(key);
			}
		}
		
		sd.setResult(changedFields);
		
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
		if (d.getAs2() != null) {
			m.put(OrganisationSetupKey.ACCESS_POINT_AS2, String.valueOf(d.getAs2()));
		}
		if (d.getAs4() != null) {
			m.put(OrganisationSetupKey.ACCESS_POINT_AS4, String.valueOf(d.getAs4()));
		}
		m.put(OrganisationSetupKey.GENERATE_INVOICE_RESPONSE_ON_ERROR, String.valueOf(d.isGenerateInvoiceResponseOnError()));
		m.put(OrganisationSetupKey.SEND_UNDELIVERABLE_INVOICE_RESPONSE_TO_ERST, String.valueOf(d.isSendUndeliverableInvoiceResponseToERST()));
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
				case ACCESS_POINT_AS2:
					d.setAs2(toId(os.getValue()));
					break;
				case ACCESS_POINT_AS4:
					d.setAs4(toId(os.getValue()));
					break;
				case GENERATE_INVOICE_RESPONSE_ON_ERROR:
					d.setGenerateInvoiceResponseOnError(Boolean.valueOf(os.getValue()));
					break;
				case SEND_UNDELIVERABLE_INVOICE_RESPONSE_TO_ERST:
					d.setSendUndeliverableInvoiceResponseToERST(Boolean.valueOf(os.getValue()));
					break;
				default:
					break;
				}
			}
		}
		if (d.getSubscribeProfileSet() == null) {
			d.setSubscribeProfileSet(Collections.emptySet());
		}
		return d;
	}

	private Long toId(String value) {
		Long id = null;
		if (value != null && StringUtils.isNumeric(value)) {
			id = Long.parseLong(value);
		}
		return id;
	}
}