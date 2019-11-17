package dk.erst.delis.web.organisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalOrganisationDaoRepository;
import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.dao.SyncOrganisationFactDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import dk.erst.delis.data.enums.access.AccessPointType;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.ValidationResultData;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.accesspoint.AccessPointService;
import dk.erst.delis.web.identifier.IdentifierService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class OrganisationController {

	@Autowired
	private OrganisationService organisationService;

	@Autowired
	private OrganisationSetupService organisationSetupService;
	
	@Autowired
	private SyncOrganisationFactDaoRepository syncOrganisationFactDaoRepository;

	@Autowired
	private JournalOrganisationDaoRepository journalOrganisationDaoRepository;

	@Autowired
	private OrganisationStatisticsService organisationStatisticsService;
	
	@Autowired
	private IdentifierLoadService identifierLoadService;

	@Autowired
	private AccessPointService accessPointService;
	
	@Autowired
	private IdentifierService identifierService;

	@Autowired
	private DocumentDaoRepository documentDaoRepository;

	@Autowired
	private SendDocumentDaoRepository sendDocumentDaoRepository;
	
	@RequestMapping("/organisation/list")
	public String list(Model model) {
		model.addAttribute("organisationList", organisationService.getOrganisations());
		model.addAttribute("orgStatMap", organisationStatisticsService.loadOrganisationIdentifierStatMap());
		return "/organisation/list";
	}
	
	@GetMapping("/organisation/create")
	public String create(Model model) {
		model.addAttribute("organisation", new Organisation());
		return "organisation/edit";
	}

	@GetMapping("/organisation/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}
		
		model.addAttribute("organisation", organisation);
		model.addAttribute("stat", organisationStatisticsService.loadOrganisationIdentifierStatMap(id));
		model.addAttribute("lastJournalList", journalOrganisationDaoRepository.findTop5ByOrganisationOrderByIdDesc(organisation));
		model.addAttribute("lastSyncFactList", syncOrganisationFactDaoRepository.findTop5ByOrganisationOrderByIdDesc(organisation));
		
		return "organisation/view";
	}

	@GetMapping("/organisation/setup/{id}")
	public String setup(@PathVariable long id, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}
		OrganisationSetupData setupData = organisationSetupService.load(organisation);

		fillSetupModel(model, organisation, setupData);
		
		return "organisation/setup";
	}

	private void fillSetupModel(Model model, Organisation organisation, OrganisationSetupData setupData) {
		model.addAttribute("as2AccessPointList", accessPointService.findAccessPointsByType(AccessPointType.AS2));
		model.addAttribute("as4AccessPointList", accessPointService.findAccessPointsByType(AccessPointType.AS4));
		model.addAttribute("organisationReceivingFormatRuleList", OrganisationReceivingFormatRule.values());
		model.addAttribute("organisationReceivingMethodList", OrganisationReceivingMethod.values());
		model.addAttribute("organisationSubscriptionProfileGroupList", OrganisationSubscriptionProfileGroup.values());

		model.addAttribute("organisation", organisation);
		model.addAttribute("organisationSetupData", setupData);
	}
	
	@PostMapping("/organisation/setup-save/{id}")
	public String setupSave(@PathVariable long id, @ModelAttribute OrganisationSetupData organisationSetupData, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}
		
		organisationSetupData.setOrganisation(organisation);
		
		ValidationResultData validationResultData = organisationSetupService.validate(organisationSetupData);
		if (!validationResultData.isAllValid()) {
			model.addAttribute("errorMessage", "Some setup fields are not valid");
			fillSetupModel(model, organisation, organisationSetupData);
			model.addAttribute("validation", validationResultData);
			return "organisation/setup";
		}
		if (organisationSetupData.isSmpIntegrationPublish() && !organisationSetupData.validatePublishReady()) {
			organisationSetupData.setSmpIntegrationPublish(false);
			
			ra.addFlashAttribute("errorMessage", "Setup is not ready for publishing: either AS2 or AS4 access point should be selected and at least one profile. SMP integration is switched back to 'Do not publish to SMP'.");
		}
		
		/*
		 * If publishing is not finished, changes to setup of publishing is forbidden
		 */
		OrganisationSetupData currentSetupData = organisationSetupService.load(organisation);
		if (organisationSetupData.isSmpIntegrationPublish() || currentSetupData.isSmpIntegrationPublish()) {
			boolean isSmpFieldsChanged = isSmpFieldsChanged(organisationSetupData, currentSetupData);
			if (isSmpFieldsChanged) {
				int countPublishingPending = identifierService.countByPublishingStatus(IdentifierPublishingStatus.PENDING, organisation);
				if (countPublishingPending > 0) {
					model.addAttribute("errorMessage", "SMP related fields cannot be changed at the moment, as publishing is in progress and there are " + countPublishingPending + " publishing pending identifiers. Please wait until publishing is finished to avoid unexpected publishing results.");
					fillSetupModel(model, organisation, organisationSetupData);
					model.addAttribute("validation", validationResultData);
					return "organisation/setup";
				}
			}
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
			sb.append("Configuration updated: ");
			sb.append(statData.toStatString());
			if (identifiersSwitchedToPending > 0) {
				sb.append(". ");
				sb.append(identifiersSwitchedToPending);
				sb.append(" identifier");
				if (identifiersSwitchedToPending > 1) {
					sb.append("s");
				}
				sb.append(" switched to PENDING publishing state.");
			}
			ra.addFlashAttribute("message", sb.toString());
		}
		
		return "redirect:/organisation/setup/"+id;
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
	
	@GetMapping("/organisation/edit/{id}")
	public String edit(@PathVariable long id, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/organisation/list";
		}
		
		model.addAttribute("organisation", organisation);
		return "organisation/edit";
	}
	
	@GetMapping("/organisation/delete/{id}")
	public String delete(@PathVariable long id, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/organisation/list";
		}
		organisation.setDeactivated(true);
		organisationService.saveOrganisation(organisation);

		return "redirect:/organisation/list";
	}

	@GetMapping("/organisation/restore/{id}")
	public String restore(@PathVariable long id, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/organisation/list";
		}
		organisation.setDeactivated(false);
		organisationService.saveOrganisation(organisation);
		
		return "redirect:/organisation/list";
	}
	
	@PostMapping("/organisation/save")
	public String save(@ModelAttribute Organisation organisation, Model model, RedirectAttributes ra) {
		if (organisation.getName() != null) {
			organisation.setName(organisation.getName().trim());
		}
		if (organisation.getCode() != null) {
			organisation.setCode(organisation.getCode().trim());
		}
		
		if (StringUtils.isEmpty(organisation.getName())) {
			model.addAttribute("errorMessage", "Name is mandatory");
			return "organisation/edit";
		}
		
		if (organisation.getName().length() > 120) {
			model.addAttribute("errorMessage", "Max length of organisation name is 120 symbols");
			return "organisation/edit";
		}

		if (StringUtils.isEmpty(organisation.getCode())) {
			model.addAttribute("errorMessage", "Code is mandatory");
			return "organisation/edit";
		}
		if (organisation.getCode().length() > 30) {
			model.addAttribute("errorMessage", "Max length of organisation code is 30 symbols");
			return "organisation/edit";
		}
		
		if (!organisation.getCode().matches("[a-z0-9]{4,30}")) {
			model.addAttribute("errorMessage", "Organisation code should be at least 4 lower case letters or digits, max length is 30 symbols");
			return "organisation/edit";
		}
		if (organisation.getId() == null) {
			Organisation existingOrganisation = organisationService.findOrganisationByCode(organisation.getCode());
			if (existingOrganisation != null) {
				model.addAttribute("errorMessage", "Organisation code should be unique, but it is already used at "+existingOrganisation.getName());
				return "organisation/edit";
			}
		} else {
			Organisation dbOrganisation = organisationService.findOrganisation(organisation.getId());
			if (!dbOrganisation.getCode().equals(organisation.getCode())) {
				Document document = documentDaoRepository.findTop1ByOrganisation(dbOrganisation);
				SendDocument sendDocument = sendDocumentDaoRepository.findTop1ByOrganisation(dbOrganisation);
				if (document != null || sendDocument != null) {
					model.addAttribute("errorMessage", "Organisation code cannot be changed after at least one document is received or sent.");
					return "organisation/edit";
				}
			}
			dbOrganisation.setName(organisation.getName());
			dbOrganisation.setCode(organisation.getCode());
			
			organisation = dbOrganisation;
		}
		
		Organisation existingName = organisationService.findOrganisationByName(organisation.getName());
		if (existingName != null) {
			boolean duplicatedName = false;
			if (organisation.getId() == null) {
				duplicatedName = true;
			} else {
				if (organisation.getId() != existingName.getId()) {
					duplicatedName = true;
				}
			}
			if (duplicatedName) {
				model.addAttribute("errorMessage", "Organisation name should be unique");
				return "organisation/edit";
			}
		}

		organisationService.saveOrganisation(organisation);

		return "redirect:/organisation/list";
	}

	@PostMapping("/organisation/upload/{id}")
	public String identifierFileUpload(@RequestParam("file") MultipartFile file, @PathVariable long id, RedirectAttributes ra) {
		if (file == null || file.isEmpty()) {
			ra.addFlashAttribute("errorMessage", "File is empty");
		} else {
			Organisation organisation = organisationService.findOrganisation(id);
			if (organisation == null) {
				ra.addFlashAttribute("errorMessage", "Organisation is not found by id " + id);
			} else {
				SyncOrganisationFact loadCSV = null;
				try {
					loadCSV = identifierLoadService.loadCSV(organisation.getCode(), file.getInputStream(), file.getOriginalFilename());
					if (loadCSV != null) {
						ra.addFlashAttribute("message", "File is loaded in " + loadCSV.getDurationMs() + " ms");
					}
				} catch (Exception e) {
					log.error("Failed to load file " + file.getOriginalFilename() + " for " + organisation.getCode(), e);
					ra.addFlashAttribute("errorMessage", e.getMessage());
				}
			}
		}
		return "redirect:/organisation/view/"+id;
	}
}
