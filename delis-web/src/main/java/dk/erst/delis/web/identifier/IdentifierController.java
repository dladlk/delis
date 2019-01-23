package dk.erst.delis.web.identifier;

import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Iterator;

@Controller
public class IdentifierController {

	@Autowired
	private IdentifierDaoRepository identifierDaoRepository;
	@Autowired
	private JournalIdentifierDaoRepository journalIdentifierDaoRepository;
	@Autowired
	private OrganisationDaoRepository organisationDaoRepository;
	

	@GetMapping("/identifier/list")
	public String listAll(Model model, RedirectAttributes redirectAttributes) {
		return list(-1, model, redirectAttributes);
	}
	
	@GetMapping("/identifier/list/{organisationId}")
	public String list(@PathVariable long organisationId, Model model, RedirectAttributes redirectAttributes) {
		Iterator<Identifier> list;
		if (organisationId == -1) {
			list = identifierDaoRepository.findAll(Sort.by("id")).iterator();
		} else {
			Organisation organisation = organisationDaoRepository.findById(organisationId).get();
			if (organisation == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Organisation is not found");
				return "redirect:/home";
			}
			
			list = identifierDaoRepository.findByOrganisation(organisation).iterator();
			model.addAttribute("organisation", organisation);
		}
		model.addAttribute("identifierList", list);
		return "identifier/list";
	}

	@PostMapping("/identifier/updatestatus")
	public String updateStatus(Identifier staleIdentifier, RedirectAttributes ra) {
		Long id = staleIdentifier.getId();
		Identifier identifier = identifierDaoRepository.findById(id).get();
		if (identifier == null) {
			ra.addFlashAttribute("errorMessage", "Identifier with ID " + id + " is not found");
			return "redirect:/home";
		}

		identifier.setStatus(staleIdentifier.getStatus());
		identifier.setPublishingStatus(staleIdentifier.getPublishingStatus());
		identifierDaoRepository.save(identifier);
		return "redirect:/identifier/view/" + id;
	}

	@GetMapping("/identifier/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Identifier identifier = identifierDaoRepository.findById(id).get();
		if (identifier == null) {
			ra.addFlashAttribute("errorMessage", "Identifier is not found");
			return "redirect:/home";
		}

		model.addAttribute("identifierStatusList", IdentifierStatus.values());
		model.addAttribute("identifierPublishingStatusList", IdentifierPublishingStatus.values());
		model.addAttribute("identifier", identifier);
		model.addAttribute("lastJournalList", journalIdentifierDaoRepository.findTop5ByIdentifierOrderByIdDesc(identifier));
		
		return "identifier/view";
	}
	
	@GetMapping("/identifier/delete/{id}")
	public String delete(@PathVariable long id, Model model, RedirectAttributes ra) {
		Identifier identifier = identifierDaoRepository.findById(id).get();
		if (identifier == null) {
			ra.addFlashAttribute("errorMessage", "Identifier is not found");
			return "redirect:/home";
		}
		
		identifier.setStatus(IdentifierStatus.DELETED);
		identifierDaoRepository.save(identifier);
		
		ra.addFlashAttribute("message", String.format("Identifier %s is marked as deleted", identifier.getUniqueValueType()));
		
		return view(id, model, ra);
	}
}
