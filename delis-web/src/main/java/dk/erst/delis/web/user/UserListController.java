package dk.erst.delis.web.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.service.EasyDatatablesListServiceImpl;
import dk.erst.delis.web.list.AbstractEasyListController;
import dk.erst.delis.web.utilities.JsonsUtility.INamed4Json;
import dk.erst.delis.web.utilities.JsonsUtility.Named4Json;

@Controller
@RequestMapping("/user")
public class UserListController extends AbstractEasyListController<User> {

	@Autowired
	private OrganisationService organisationService;
	/*
	 * START EasyDatatables block
	 */
	@Autowired
	private UserDataTableRepository userDataTableRepository;
	@Autowired
	private EasyDatatablesListServiceImpl<User> userEasyDatatablesListService;

	@Override
	protected String getListCode() {
		return "user";
	}

	@Override
	protected DataTablesRepository<User, Long> getDataTableRepository() {
		return this.userDataTableRepository;
	}

	@Override
	protected EasyDatatablesListService<User> getEasyDatatablesListService() {
		return userEasyDatatablesListService;
	}

	private enum UserDisabledStatus implements INamed4Json {
		DISABLED, ACTIVE;
		
		@Override
		public String getName() {
			return StringUtils.capitalize(this.name().toLowerCase());
		}
		
		@Override
		public String getValue() {
			return String.valueOf(this == DISABLED); 
		}
	}
	
	private enum FormHiddenStatus implements INamed4Json {
		HIDDEN, SHOWN;
		
		@Override
		public String getName() {
			return StringUtils.capitalize(this.name().toLowerCase());
		}
		
		@Override
		public String getValue() {
			return String.valueOf(this == HIDDEN);
		}
		
	}
	
	@RequestMapping("/list")
	public String list(Model model, WebRequest webRequest) {
		model.addAttribute("organisationList", buildSelectList(organisationService.getOrganisations()));
		model.addAttribute("userStatusList", UserDisabledStatus.values());
		model.addAttribute("shownHiddenStatusList", FormHiddenStatus.values());
		return super.list(model, webRequest);
	}

	private List<INamed4Json> buildSelectList(Iterable<Organisation> organisations) {
		List<INamed4Json> list = new ArrayList<INamed4Json>();
		list.add(Named4Json.of("Administrators", "0"));
		organisations.forEach(c -> {
			list.add(Named4Json.of(c.getName(), String.valueOf(c.getId())));
		});
		return list;
	}

	/*
	 * END EasyDatatables block
	 */	

}
