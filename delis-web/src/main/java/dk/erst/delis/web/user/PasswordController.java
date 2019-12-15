package dk.erst.delis.web.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.config.web.security.CustomUserDetails;
import dk.erst.delis.data.entities.user.User;
import lombok.Getter;
import lombok.Setter;

@Controller
public class PasswordController {

	@Autowired
	private UserService userService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.addValidators(new PasswordFormValidator());
	}

	@GetMapping("/user/password")
	public String passwordForm(Model model, RedirectAttributes ra) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "user/password";
	}

	@Getter
	@Setter
	public static class PasswordForm {
		private String oldPassword;
		private String password;
		private String password2;
	}

	@PostMapping("/user/password")
	public String passwordSave(@Valid @ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult bindingResult, Authentication authentication, Model model, RedirectAttributes ra) {
		User user = null;
		if (!bindingResult.hasErrors()) {
			CustomUserDetails cud = (CustomUserDetails) authentication.getPrincipal();
			Long userId = cud.getId();
			user = userService.findById(userId);
			userService.validatePassword(user, passwordForm, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Some fields are not valid.");
			return "user/password";
		}

		userService.updatePassword(user, passwordForm.getPassword());

		ra.addFlashAttribute("message", "Your password is successfully updated.");
		return "redirect:/home";
	}
}
