package dk.erst.delis.web.organisation;

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

import dk.erst.delis.dao.JournalOrganisationDaoRepository;
import dk.erst.delis.dao.SyncOrganisationFactDaoRepository;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.SyncOrganisationFact;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class OrganisationController {

	@Autowired
	private OrganisationService organisationService;
	
	@Autowired
	private SyncOrganisationFactDaoRepository syncOrganisationFactDaoRepository;

	@Autowired
	private JournalOrganisationDaoRepository journalOrganisationDaoRepository;

	@Autowired
	private OrganisationStatisticsService organisationStatisticsService;
	
	@Autowired
	private IdentifierLoadService identifierLoadService;

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
		
		model.addAttribute("organisation", organisation);
		return "organisation/setup";
	}
	
	@PostMapping("/organisation/save")
	public String save(@ModelAttribute Organisation organisation, Model model, RedirectAttributes ra) {
		if (StringUtils.isEmpty(organisation.getName())) {
			model.addAttribute("errorMessage", "Name is mandatory");
			return "organisation/edit";
		}

		organisationService.saveOrganisation(organisation);

		return "redirect:/home";
	}

	@PostMapping("/organisation/upload/{id}")
	public String identifierFileUpload(@RequestParam("file") MultipartFile file, @PathVariable long id, RedirectAttributes ra, Model model) {
		if (file == null || file.isEmpty()) {
			ra.addAttribute("errorMessage", "File is empty");
		} else {
			Organisation organisation = organisationService.findOrganisation(id);
			if (organisation == null) {
				ra.addAttribute("errorMessage", "Organisation is not found by id " + id);
			} else {

				SyncOrganisationFact loadCSV = null;
				try {
					loadCSV = identifierLoadService.loadCSV(organisation.getCode(), file.getInputStream(), file.getOriginalFilename());
				} catch (Exception e) {
					log.error("Failed to load file " + file.getOriginalFilename() + " for " + organisation.getCode(), e);
					ra.addAttribute("errorMessage", e.getMessage());
				}
				if (loadCSV != null) {
					ra.addAttribute("infoMessage", "File is loaded in " + loadCSV.getDurationMs() + " ms" + ", total ");
				}
			}
		}
		return "redirect:/organisation/view/"+id;
	}
}
