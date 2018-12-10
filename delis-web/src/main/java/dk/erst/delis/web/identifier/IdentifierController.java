package dk.erst.delis.web.identifier;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.dao.IdentifierRepository;
import dk.erst.delis.dao.JournalIdentifierRepository;
import dk.erst.delis.dao.OrganisationRepository;
import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.Organisation;

@Controller
public class IdentifierController {

	@Autowired
	private IdentifierRepository identifierRepository;
	@Autowired
	private JournalIdentifierRepository journalIdentifierRepository;
	@Autowired
	private OrganisationRepository organisationRepository;
	
	@GetMapping("/identifier/list/{organisationId}")
	public String list(@PathVariable long organisationId, Model model, RedirectAttributes redirectAttributes) {
		Organisation organisation = organisationRepository.findById(organisationId).get();
		if (organisation == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}
		
		List<Identifier> list = identifierRepository.findByOrganisation(organisation);
		model.addAttribute("identifierList", list);
		model.addAttribute("organisation", organisation);
		return "identifier/list";
	}
	
	@GetMapping("/identifier/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Identifier identifier = identifierRepository.findById(id).get();
		if (identifier == null) {
			ra.addFlashAttribute("errorMessage", "Identifier is not found");
			return "redirect:/home";
		}
		
		model.addAttribute("identifier", identifier);
		model.addAttribute("lastJournalList", journalIdentifierRepository.findTop5ByIdentifierOrderByIdDesc(identifier));
		
		return "identifier/view";
	}
}
