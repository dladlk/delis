package dk.erst.delis.web.organisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.accesspoint.AccessPointService;
import dk.erst.delis.web.identifier.IdentifierService;

@Controller
public class OrganisationSetupController {

	@Autowired
	private OrganisationService organisationService;

	@Autowired
	private OrganisationSetupService organisationSetupService;

	@Autowired
	private IdentifierService identifierService;
	
	@Autowired
	private AccessPointService accessPointService;
	
	@PostMapping("/organisation/setup-save/{id}")
	public String setupSave(@PathVariable long id, @Valid @ModelAttribute("organisationSetupData") OrganisationSetupData organisationSetupData, BindingResult bindingResult, 
			Model model, RedirectAttributes ra, @RequestParam(required = false, name = "validateReceivingSetup") boolean validateReceivingSetup
			) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}
		organisationSetupData.setOrganisation(organisation);
		if (!bindingResult.hasErrors()) {
			organisationSetupService.validate(organisationSetupData, bindingResult, validateReceivingSetup);
		}

		OrganisationSetupData currentSetupData = organisationSetupService.load(organisation);
		if (!bindingResult.hasErrors()) {
			/*
			 * If publishing is not finished, changes to setup of publishing is forbidden
			 */
			if (organisationSetupData.isSmpIntegrationPublish() || currentSetupData.isSmpIntegrationPublish()) {
				boolean isSmpFieldsChanged = isSmpFieldsChanged(organisationSetupData, currentSetupData);
				if (isSmpFieldsChanged) {
					int countPublishingPending = identifierService.countByPublishingStatus(IdentifierPublishingStatus.PENDING, organisation);
					if (countPublishingPending > 0) {
						model.addAttribute("errorMessage", "SMP related fields cannot be changed at the moment, as publishing is in progress and there are " + countPublishingPending + " publishing pending identifiers. Please wait until publishing is finished to avoid unexpected publishing results.");
						OrganisationController.fillSetupModel(model, organisation, organisationSetupData, accessPointService, validateReceivingSetup);
						return "organisation/setup";
					}
				}
			}
		}
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some setup fields are not valid");
			OrganisationController.fillSetupModel(model, organisation, organisationSetupData, accessPointService, validateReceivingSetup);
			return "organisation/setup";
		}
		if (organisationSetupData.isSmpIntegrationPublish() && !organisationSetupData.validatePublishReady()) {
			organisationSetupData.setSmpIntegrationPublish(false);
			
			ra.addFlashAttribute("errorMessage", "Setup is not ready for publishing: either AS2 or AS4 access point should be selected and at least one profile. SMP integration is switched back to 'Do not publish to SMP'.");
		}
		
		StatData statData = organisationSetupService.update(organisationSetupData);
		int identifiersSwitchedToPending = 0;
		if (statData.getResult() != null) {
			@SuppressWarnings("unchecked")
			List<OrganisationSetupKey> changedFields = (List<OrganisationSetupKey>) statData.getResult();
			if (organisationSetupData.isSmpIntegrationPublish() && isSmpFieldsChanged(changedFields)) {
				identifiersSwitchedToPending = markActiveIdentifiersPending(organisation, false);
			} else if (!organisationSetupData.isSmpIntegrationPublish() && currentSetupData.isSmpIntegrationPublish()) {
				/*
				 * Switched from publish to not publish - we need to delete from publishing and cleanup publishing status
				 */
				identifiersSwitchedToPending = markActiveIdentifiersPending(organisation, true);
			}
		}
		if (statData.isEmpty()) {
			ra.addFlashAttribute("message", "Nothing is changed");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Configuration is updated.");
			if (identifiersSwitchedToPending > 0) {
				sb.append(" ");
				sb.append(identifiersSwitchedToPending);
				sb.append(" identifier");
				if (identifiersSwitchedToPending > 1) {
					sb.append("s");
				}
				sb.append(" switched to PENDING publishing state.");
			}
			if (validateReceivingSetup && organisationSetupData.getReceivingMethod() != null) {
				sb.append(" Receiving setup is validated for accessibility successfully.");
			}
			ra.addFlashAttribute("message", sb.toString());
			
		}
		
		return "redirect:/organisation/setup/"+id;
	}
	
	private boolean isSmpFieldsChanged(List<OrganisationSetupKey> changedFields) {
		List<OrganisationSetupKey> smpRelatedFields = Arrays.asList(new OrganisationSetupKey[] { 
				OrganisationSetupKey.SUBSCRIBED_SMP_PROFILES,
				OrganisationSetupKey.ACCESS_POINT_AS2,
				OrganisationSetupKey.ACCESS_POINT_AS4,
				OrganisationSetupKey.SMP_INTEGRATION,
		}
		);
		return !Collections.disjoint(smpRelatedFields, changedFields);
	}

	private int markActiveIdentifiersPending(Organisation organisation, boolean excludeFailed) {
		Iterator<Identifier> identifiers = identifierService.findByOrganisation(organisation);
		List<Long> idsForUpdate = new ArrayList<>();
		identifiers.forEachRemaining(identifier -> {
			if (identifier.getStatus() == IdentifierStatus.ACTIVE) {
				if (!excludeFailed || identifier.getPublishingStatus() != IdentifierPublishingStatus.FAILED) {
					idsForUpdate.add(identifier.getId());
				}
			}
		});
		return identifierService.updateStatuses(idsForUpdate, IdentifierStatus.ACTIVE, IdentifierPublishingStatus.PENDING, "Pending publishing after change of organisation setup");
	}
	
	private boolean isSmpFieldsChanged(OrganisationSetupData newSetupData, OrganisationSetupData currentSetupData) {
		if (currentSetupData.getAs2() != newSetupData.getAs2()) {
			return true;
		}
		if (currentSetupData.getAs4() != newSetupData.getAs4()) {
			return true;
		}
		if (currentSetupData.isSmpIntegrationPublish() != newSetupData.isSmpIntegrationPublish()) {
			return true;
		}
		Set<OrganisationSubscriptionProfileGroup> oldSet = currentSetupData.getSubscribeProfileSet();
		Set<OrganisationSubscriptionProfileGroup> newSet = newSetupData.getSubscribeProfileSet();
		if (!oldSet.containsAll(newSet) || !newSet.containsAll(oldSet)) {
			return true;
		}
		return false;
	}


}
