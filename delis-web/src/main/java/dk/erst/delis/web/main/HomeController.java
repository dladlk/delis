package dk.erst.delis.web.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.erst.delis.web.organisation.OrganisationService;
import dk.erst.delis.web.organisation.OrganisationStatisticsService;

@Controller
public class HomeController {

	@Autowired
	private OrganisationService organisationService;
	@Autowired
	private OrganisationStatisticsService organisationStatisticsService;

	@RequestMapping("/home")
	public String index(Model model, Authentication authentication) {
		model.addAttribute("organisationList", organisationService.getOrganisations());
		model.addAttribute("orgStatMap", organisationStatisticsService.loadOrganisationIdentifierStatMap());
		return "home";
	}

}
