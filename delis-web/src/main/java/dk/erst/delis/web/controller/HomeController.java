package dk.erst.delis.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@Autowired
	private dk.erst.delis.web.organisation.OrganisationService organisationService;

	@RequestMapping("/home")
	public String index(Model model, Authentication authentication) {
		model.addAttribute("organisationList", organisationService.getOrganisations());
		return "home";
	}

}
