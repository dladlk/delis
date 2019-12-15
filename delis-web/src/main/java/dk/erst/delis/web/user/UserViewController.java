package dk.erst.delis.web.user;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.user.User;

@Controller
public class UserViewController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/user/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		User user = userService.findById(id);
		if (user == null) {
			ra.addFlashAttribute("errorMessage", "User is not found");
			return "redirect:/user/list";
		}
		model.addAttribute("user", user);
		return "user/view";
	}

	@GetMapping("/user/unlock/{id}")
	public String unlock(@PathVariable long id, Model model, RedirectAttributes ra) {
		User user = userService.findById(id);
		if (user == null) {
			ra.addFlashAttribute("errorMessage", "User is not found");
			return "redirect:/user/list";
		}

		userRepository.resetInvalidLoginCount(user.getUsername(), Calendar.getInstance().getTime());

		ra.addFlashAttribute("message", "User account is unlocked: number of invalid attempts is reset.");
		return "redirect:/user/view/" + user.getId();
	}

	@GetMapping("/user/deactivate/{id}")
	public String deactivate(@PathVariable long id, RedirectAttributes ra) {
		if (userService.deactivateUser(id)) {
			ra.addFlashAttribute("message", "User is deactivated");
		}
		return "redirect:/user/view/" + id;
	}

	@GetMapping("/user/activate/{id}")
	public String activate(@PathVariable long id, RedirectAttributes ra) {
		if (userService.activateUser(id)) {
			ra.addFlashAttribute("message", "User is activated");
		}
		return "redirect:/user/view/" + id;
	}

}
