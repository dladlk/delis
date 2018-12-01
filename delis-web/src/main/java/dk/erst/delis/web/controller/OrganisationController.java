package dk.erst.delis.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.Organisation;
import dk.erst.delis.web.organisation.OrganisationService;

@Controller
public class OrganisationController {

	@Autowired
	private OrganisationService organisationService; 
	
	@GetMapping("/organisation/create")
	public String create(Model model) throws IOException {
		model.addAttribute("organisation", new Organisation());
		return "organisation/edit";
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
}
