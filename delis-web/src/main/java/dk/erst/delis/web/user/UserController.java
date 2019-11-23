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

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

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
		return "user/edit";
	}

	@GetMapping("/update/{id}")
	public String update(@PathVariable long id, Model model) {
		User user = userService.findById(id);
		UserData userData = new UserData();
		BeanUtils.copyProperties(user, userData, "disabledIrForm");
		if (user.getOrganisation() != null) {
			userData.setOrganisationCode(user.getOrganisation().getCode());
		}
		if (user.getDisabledIrForm() != null && user.getDisabledIrForm().booleanValue()) {
			userData.setDisabledIrForm(true);
		}
		model.addAttribute("user", userData);
		return "user/edit";
	}

	@GetMapping("/delete/{id}")
	public String disable(@PathVariable long id, RedirectAttributes ra) {
		return "redirect:/user/list";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("user") UserData user, BindingResult bindingResult, Model model, RedirectAttributes ra) {
		if (!bindingResult.hasErrors()) {
			userService.validate(user, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some fields are not valid.");
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
