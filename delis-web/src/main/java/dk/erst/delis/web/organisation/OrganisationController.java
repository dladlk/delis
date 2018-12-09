package dk.erst.delis.web.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	private IdentifierLoadService identifierLoadService;

	@GetMapping("/organisation/create")
	public String create(Model model) {
		model.addAttribute("organisation", new Organisation());
		return "organisation/edit";
	}

	@GetMapping("/organisation/view/{id}")
	public String view(@PathVariable long id, Model model) {
		OrganisationData od = organisationService.loadOrganisationData(id);
		model.addAttribute("organisation", od.getOrganisation());
		model.addAttribute("organisationData", od);
		return "organisation/view";
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
	public String identifierFileUpload(@RequestParam("file") MultipartFile file, @PathVariable long id, RedirectAttributes redirectAttributes, Model model) {
		if (file == null || file.isEmpty()) {
			model.addAttribute("errorMessage", "File is empty");
		} else {
			Organisation organisation = organisationService.findOrganisation(id);
			if (organisation == null) {
				model.addAttribute("errorMessage", "Organisation is not found by id " + id);
			} else {

				SyncOrganisationFact loadCSV = null;
				try {
					loadCSV = identifierLoadService.loadCSV(organisation.getCode(), file.getInputStream(), file.getOriginalFilename());
				} catch (Exception e) {
					log.error("Failed to load file " + file.getOriginalFilename() + " for " + organisation.getCode(), e);
					model.addAttribute("errorMessage", e.getMessage());
				}
				if (loadCSV != null) {
					model.addAttribute("infoMessage", "File is loaded in " + loadCSV.getDurationMs() + " ms" + ", total ");
				}
			}
		}
		return view(id, model);
	}
}
