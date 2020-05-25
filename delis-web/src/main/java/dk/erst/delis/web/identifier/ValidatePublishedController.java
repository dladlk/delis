package dk.erst.delis.web.identifier;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.task.identifier.publish.ValidatePublishedService;
import dk.erst.delis.task.identifier.publish.ValidatePublishedTreeBuilder;
import dk.erst.delis.task.identifier.publish.ValidatePublishedTreeBuilder.TreeNode;
import dk.erst.delis.task.identifier.publish.ValidatePublishedService.ValidatePublishedResult;
import dk.erst.delis.task.organisation.OrganisationService;

@Controller
public class ValidatePublishedController {

	@Autowired
	private OrganisationService organisationService;

	private static final Map<Long, ValidatePublishedResult> CACHE = new HashMap<Long, ValidatePublishedResult>();

	private boolean useCache = false;

	@Autowired
	private ValidatePublishedService validatePublishedService;

	@GetMapping("/organisation/validate/{organisationId}")
	public String validate(@PathVariable long organisationId, Model model, RedirectAttributes ra) {
		Organisation organisation = organisationService.findOrganisation(organisationId);
		if (organisation == null) {
			ra.addFlashAttribute("errorMessage", "Organisation is not found");
			return "redirect:/home";
		}

		model.addAttribute("organisation", organisation);

		ValidatePublishedResult resultList;
		if (useCache) {
			if (CACHE.containsKey(organisation.getId())) {
				resultList = CACHE.get(organisation.getId());
			}
		}
		resultList = new ValidatePublishedResult();
		validatePublishedService.validatePublishedIdentifiers(organisation, resultList);
		if (useCache) {
			CACHE.put(organisation.getId(), resultList);
		}

		model.addAttribute("resultList", resultList);
		TreeNode expectedTree = ValidatePublishedTreeBuilder.buildExpectedTree(resultList);
		model.addAttribute("expectedTree", expectedTree);
		ValidatePublishedTreeBuilder.buildActualTreeList(resultList, expectedTree);

		return "/organisation/validate";
	}

}
