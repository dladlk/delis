package dk.erst.delis.web.organisation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
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

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalOrganisationDaoRepository;
import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.dao.SyncOrganisationFactDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import dk.erst.delis.data.enums.access.AccessPointType;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.accesspoint.AccessPointService;
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

		fillSetupModel(model, organisation, setupData, accessPointService, false);
		
		return "organisation/setup";
	}

	protected static void fillSetupModel(Model model, Organisation organisation, OrganisationSetupData setupData, AccessPointService accessPointService, boolean validateReceivingSetup) {
		model.addAttribute("as2AccessPointList", accessPointService.findAccessPointsByType(AccessPointType.AS2));
		model.addAttribute("as4AccessPointList", accessPointService.findAccessPointsByType(AccessPointType.AS4));
		model.addAttribute("organisationReceivingFormatRuleList", OrganisationReceivingFormatRule.values());
		model.addAttribute("organisationReceivingMethodList", OrganisationReceivingMethod.values());
		model.addAttribute("organisationSubscriptionProfileGroupList", OrganisationSubscriptionProfileGroup.values());

		model.addAttribute("organisation", organisation);
		model.addAttribute("organisationSetupData", setupData);

		model.addAttribute("validateReceivingSetup", validateReceivingSetup);
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
	
	@GetMapping("/organisation/download/{id}")
	public ResponseEntity<Object> identifierFileUpload(@PathVariable long id, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(id);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found by id " + id);
			return RedirectUtil.redirectEntity("/organisation/list");
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.identifierLoadService.exportIdentifierList(organisation, out);
		byte[] data = out.toByteArray();
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + organisation.getCode() + "_" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + ".csv\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
	}
}
