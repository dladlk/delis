package dk.erst.delis.task.identifier;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IdentifierCheckService {

	private IdentifierResolverService identifierResolverService;

	private OrganisationSetupService organisationSetupService;

	@Autowired
	public IdentifierCheckService(IdentifierResolverService identifierResolverService, OrganisationSetupService organisationSetupService) {
		this.identifierResolverService = identifierResolverService;
		this.organisationSetupService = organisationSetupService;
	}

	public static class Result implements Serializable {
		
		private static final long serialVersionUID = 618279023669828082L;
		
		final boolean success;
		final String description;

		public Result(boolean success, String description) {
			this.success = success;
			this.description = description;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getDescription() {
			return description;
		}
	}

	public Result checkReceivingSupport(String compoundIdentifier, String service, String action, boolean skipServiceStep, boolean skipActionStep) {
		long startTime = Calendar.getInstance().getTimeInMillis();

		log.info("Start checkReceivingSupport for identifier '" + compoundIdentifier + "', service '" + service + "', action '" + action + "', skipService=" + skipServiceStep + ", skipAction=" + skipActionStep);

		Identifier identifier = getIdentifier(compoundIdentifier);

		Result result = checkIdentifier(identifier, compoundIdentifier);

		if (result.isSuccess()) {
			if (skipServiceStep && skipActionStep) {
				log.info("Identifier found. Service and Action validation skipped.");
			} else {
				Set<OrganisationSubscriptionProfileGroup> availableServices = getAvailableServices(identifier, service, skipServiceStep);
				if (availableServices.size() == 0) {
					log.info("No Service found for related organisation");
					result = new Result(false, "No Service found for related organisation");
				} else {
					Set<String> availableActions = getAvailableActions(availableServices, action, skipActionStep);
					if (availableActions.size() == 0) {
						log.info("No Action found for related organisation");
						result = new Result(false, "No Action found for related organisation");
					}
				}
			}
		}

		long stopTime = Calendar.getInstance().getTimeInMillis();
		log.info("Stop checkIdentifier. Execution time: " + (stopTime - startTime) + " ms.");
		return result;
	}

	private Result checkIdentifier(Identifier identifier, String originalIdString) {
		boolean success = true;
		String description = "ok";
		log.info("Check identifier");
		if (identifier == null) {
			description = "Identifier '" + originalIdString + "' does not exist";
			success = false;
		} else if (identifier.getStatus() == IdentifierStatus.DELETED) {
			description = "Identifier " + originalIdString + " marked as deleted";
			log.info(description);
			success = false;
		}
		log.info("Identifier '" + originalIdString + "' check performed: " + description);
		return new Result(success, description);
	}

	private Identifier getIdentifier(String compoundIdentifier) {
		Identifier identifier = null;
		if (compoundIdentifier == null) {
			return null;
		}

		int index = compoundIdentifier.lastIndexOf(":");

		if (index < 0 || index >= compoundIdentifier.length() - 1) {
			log.warn("Passed identifier value has invalid structure: '" + compoundIdentifier + "'");
			return null;
		}

		String type = compoundIdentifier.substring(0, index);
		String id = compoundIdentifier.substring(index + 1);

		log.info("Identifier type: " + type + " Identifier id: " + id);
		identifier = resolveIdentifier(type, id);
		String idName = identifier == null ? "null" : identifier.getName();
		log.info("Found identifier: " + idName);
		return identifier;
	}

	protected Identifier resolveIdentifier(String type, String id) {
		return identifierResolverService.resolve(type, id);
	}

	private Set<OrganisationSubscriptionProfileGroup> getAvailableServices(Identifier identifier, String service, boolean skipServiceStep) {
		Set<OrganisationSubscriptionProfileGroup> resultSet = new HashSet<>();

		Organisation organisation = identifier.getOrganisation();
		OrganisationSetupData setupData = loadOrganisationSetup(organisation);
		Set<OrganisationSubscriptionProfileGroup> orgSubscribedProfiles = setupData.getSubscribeProfileSet();

		log.info("Supported organisation profiles: " + orgSubscribedProfiles);

		if (skipServiceStep) {
			String allServices = orgSubscribedProfiles.stream().map(OrganisationSubscriptionProfileGroup::getCode).collect(Collectors.joining(","));
			log.info("Check service skip... Return all available services: " + allServices);
			resultSet.addAll(orgSubscribedProfiles);
		} else {
			final String processIdentifier = extractProcessIdentifier(service);
			log.info("Check processIdentifier '" + processIdentifier + "'");

			resultSet = orgSubscribedProfiles.stream().filter(s -> s.getProcessId().equalsIgnoreCase(processIdentifier)).collect(Collectors.toSet());
			log.info("Found " + resultSet);

		}
		return resultSet;
	}

	protected OrganisationSetupData loadOrganisationSetup(Organisation organisation) {
		return organisationSetupService.load(organisation);
	}

	private static String extractProcessIdentifier(String service) {
		String processIdentifier = service;
		if (processIdentifier != null) {
			int schemeDelimiterIndex = processIdentifier.indexOf("::");
			if (schemeDelimiterIndex > 0 && schemeDelimiterIndex < processIdentifier.length() - 2) {
				processIdentifier = processIdentifier.substring(schemeDelimiterIndex + 2);
			}
		}
		return processIdentifier;
	}

	private Set<String> getAvailableActions(Set<OrganisationSubscriptionProfileGroup> services, String action, boolean skipActionStep) {
		Set<String> all = new HashSet<>();
		Set<String> result = new HashSet<>();
		for (OrganisationSubscriptionProfileGroup group : services) {
			String[] documentIdentifiers = group.getDocumentIdentifiers();
			all.addAll(Arrays.asList(documentIdentifiers));
		}

		if (skipActionStep) {
			log.info("Check action skip... Return all available actions for all available services");
			result = all;
			return result;
		} else {
			log.info("Looking for action " + action);
			for (String availableAction : all) {
				if (action.endsWith(availableAction)) {
					result.add(availableAction);
				}
			}
			log.info("Found " + result.size());
		}
		return result;
	}
}
