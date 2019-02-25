package dk.erst.delis.web.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.web.organisation.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Iterator;
import java.util.List;

@Controller
public class IdentifierController {

	@Autowired
	private IdentifierService identifierService;
	@Autowired
	private OrganisationService organisationService;


	@GetMapping("/identifier/list")
	public String listAll(Model model, RedirectAttributes redirectAttributes) {
		return list(-1, model, redirectAttributes);
	}
	
	@GetMapping("/identifier/list/{organisationId}")
	public String list(@PathVariable long organisationId, Model model, RedirectAttributes redirectAttributes) {
		Iterator<Identifier> list;
		if (organisationId == -1) {
			list = identifierService.findAll();
		} else {
			Organisation organisation = organisationService.findOrganisation(organisationId);
			if (organisation == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Organisation is not found");
				return "redirect:/home";
			}
			
			list = identifierService.findByOrganisation(organisation);
			model.addAttribute("organisation", organisation);
		}
		model.addAttribute("identifierList", list);
		model.addAttribute("selectedIdList", new IdentifierStatusBachUdpateInfo());
		model.addAttribute("statusList", IdentifierStatus.values());
		model.addAttribute("publishingStatusList", IdentifierPublishingStatus.values());
		return "identifier/list";
	}

	@PostMapping("/identifier/updatestatuses")
	public String listFilter(@ModelAttribute IdentifierStatusBachUdpateInfo idList, Model model) {
		List<Long> ids = idList.getIdList();
		IdentifierStatus status = idList.getStatus();
		IdentifierPublishingStatus publishStatus = idList.getPublishStatus();
		identifierService.updateStatuses(ids, status, publishStatus);
		return "redirect:/identifier/list";
	}

	@PostMapping("/identifier/updatestatus")
	public String updateStatus(Identifier staleIdentifier, RedirectAttributes ra) {
		Long id = staleIdentifier.getId();
		int i = identifierService.updateStatus(id, staleIdentifier.getStatus(), staleIdentifier.getPublishingStatus());
		if (i == 0) {
			ra.addFlashAttribute("errorMessage", "Identifier with ID " + id + " is not found");
			return "redirect:/identifier/list";
		}
		return "redirect:/identifier/view/" + id;
	}

	@GetMapping("/identifier/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Identifier identifier = identifierService.findOne(id);
		if (identifier == null) {
			ra.addFlashAttribute("errorMessage", "Identifier is not found");
			return "redirect:/home";
		}

		model.addAttribute("identifierStatusList", IdentifierStatus.values());
		model.addAttribute("identifierPublishingStatusList", IdentifierPublishingStatus.values());
		model.addAttribute("identifier", identifier);
		model.addAttribute("lastJournalList", identifierService.getJournalRecords(identifier));
		
		return "identifier/view";
	}
	
	@GetMapping("/identifier/delete/{id}")
	public String delete(@PathVariable long id, Model model, RedirectAttributes ra) {
		int count = identifierService.markAsDeleted(id);
		if (count == 0) {
			ra.addFlashAttribute("errorMessage", "Identifier is not found");
			return "redirect:/home";
		}
		ra.addFlashAttribute("message", String.format("Identifier %s is marked as deleted", identifierService.findOne(id).getUniqueValueType()));
		return view(id, model, ra);
	}
}
