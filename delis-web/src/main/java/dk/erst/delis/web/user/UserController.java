package dk.erst.delis.web.user;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.task.organisation.OrganisationService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrganisationService organisationService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
    	StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, stringtrimmer);    	
        binder.addValidators(new UserDataValidator());
    }
	
	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("users", userService.findAll());
		return "user/list";
	}

	@GetMapping("/create")
	public String create(Model model) {
		model.addAttribute("user", new UserData());
		fillModel(model);
		return "user/edit";
	}

	private void fillModel(Model model) {
		model.addAttribute("organisationList", organisationService.getOrganisations());
	}

	@GetMapping("/update/{id}")
	public String update(@PathVariable long id, Model model) {
		User user = userService.findById(id);
		UserData userData = new UserData();
		BeanUtils.copyProperties(user, userData, "disabledIrForm");
		if (user.getOrganisation() != null) {
			userData.setOrganisationCode(user.getOrganisation().getCode());
		} else {
			userData.setAdmin(true);
		}
		if (user.getDisabledIrForm() != null && user.getDisabledIrForm().booleanValue()) {
			userData.setDisabledIrForm(true);
		}
		model.addAttribute("user", userData);
		fillModel(model);
		return "user/edit";
	}

	@GetMapping("/deactivate/{id}")
	public String deactivate(@PathVariable long id, RedirectAttributes ra) {
		if (userService.deactivateUser(id)) {
			ra.addFlashAttribute("message", "User is deactivated");
		}
		return "redirect:/user/list";
	}

	@GetMapping("/activate/{id}")
	public String activate(@PathVariable long id, RedirectAttributes ra) {
		if (userService.activateUser(id)) {
			ra.addFlashAttribute("message", "User is activated");
		}
		return "redirect:/user/list";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("user") UserData user, BindingResult bindingResult, Model model, RedirectAttributes ra) {
		if (!bindingResult.hasErrors()) {
			userService.validate(user, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some fields are not valid.");
			fillModel(model);
			return "user/edit";
		}
		
		userService.saveOrUpdateUser(user);
		
		if (user.isNew()) {
			ra.addFlashAttribute("message", "User is created.");
		} else {
			ra.addFlashAttribute("message", "User is updated.");
		}
		
		return "redirect:/user/list";
	}
}
