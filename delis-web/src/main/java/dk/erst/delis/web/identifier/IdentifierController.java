package dk.erst.delis.web.identifier;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.config.web.security.CustomUserDetails;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfile;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.service.EasyDatatablesListServiceImpl;
import dk.erst.delis.web.list.AbstractEasyListController;

@Controller
public class IdentifierController extends AbstractEasyListController<Identifier> {

	@Autowired
	private IdentifierService identifierService;
	@Autowired
	private OrganisationService organisationService;

	/*
	 * START EasyDatatables block
	 */
	@Autowired
	private IdentifierDataTableRepository identifierDataTableRepository;
	@Autowired
	private EasyDatatablesListServiceImpl<Identifier> identifierEasyDatatablesListService;
	
	@Override
	protected String getListCode() {
		return "identifier";
	}
	@Override
	protected DataTablesRepository<Identifier, Long> getDataTableRepository() {
		return this.identifierDataTableRepository;
	}
	@Override
	protected EasyDatatablesListService<Identifier> getEasyDatatablesListService() {
		return identifierEasyDatatablesListService;
	}

	@RequestMapping("/identifier/list")
	public String list(Model model, WebRequest webRequest) {
		model.addAttribute("selectedIdList", new IdentifierStatusBatchUpdateInfo());
		model.addAttribute("statusList", IdentifierStatus.values());
		model.addAttribute("publishingStatusList", IdentifierPublishingStatus.values());
		model.addAttribute("organisationList", organisationService.getOrganisations());
		
		return super.list(model, webRequest);
	}
	
	/*
	 * END EasyDatatables block
	 */
	
	
	@GetMapping("/identifier/listOld")
	public String listAll(Model model, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails cud = (CustomUserDetails) authentication.getPrincipal();
		return list(cud.getOrganisationId(), model, redirectAttributes);
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
		model.addAttribute("selectedIdList", new IdentifierStatusBatchUpdateInfo());
		model.addAttribute("statusList", IdentifierStatus.values());
		model.addAttribute("publishingStatusList", IdentifierPublishingStatus.values());
		return "identifier/list";
	}

	@PostMapping("/identifier/updatestatuses")
	public String updateStatuses(@ModelAttribute IdentifierStatusBatchUpdateInfo idList, Model model) {
		List<Long> ids = idList.getIdList();
		IdentifierStatus status = idList.getStatusNew();
		IdentifierPublishingStatus publishStatus = idList.getPublishStatusNew();
		identifierService.updateStatuses(ids, status, publishStatus, null);
		return "redirect:/identifier/list";
	}

	@PostMapping("/identifier/updatestatus")
	public String updateStatus(Identifier staleIdentifier, RedirectAttributes ra) {
		Long id = staleIdentifier.getId();
		int i = identifierService.updateStatus(id, staleIdentifier.getStatus(), staleIdentifier.getPublishingStatus(), null);
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

	public String replaceIdentifierWithCode(String message) {
		if (StringUtils.isEmpty(message) || message.length() < 30) {
			return message;
		}
		OrganisationSubscriptionProfile[] profiles = OrganisationSubscriptionProfile.values();
		String newMessage = message;
		for (OrganisationSubscriptionProfile profile : profiles) {
			newMessage = message.replace(profile.getDocumentIdentifier(), "<span class='delis-profile-code' title='"+profile.getDocumentIdentifier()+"'>"+profile.getName()+"</span>");
			if (message != newMessage) {
				break;
			}
		}
		return newMessage;
	}
}
