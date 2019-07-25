package dk.erst.delis.web.user;

import javax.validation.Valid;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.task.organisation.OrganisationService;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
    private UserService userService;
	
	@Autowired
	private OrganisationService organisationService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/create")
    public String createNewUser(Model model) {
        model.addAttribute("user", new UserData());
        return "user/edit";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable long id, Model model) {
        User user = userService.findById(id);
        UserData userData = new UserData();
        BeanUtils.copyProperties(user, userData);
        if (user.getOrganisation() != null) {
        	userData.setOrganisationCode(user.getOrganisation().getCode());
        }
        model.addAttribute("user", userData);
        return "user/update";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable long id, Model model) {
        userService.deleteUser(id);
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @PostMapping("/create")
    public String createNewUser(@Valid UserData user, RedirectAttributes ra) {
        if (StringUtils.isBlank(user.getUsername())) {
            ra.addFlashAttribute("errorMessage", "field username can't be empty");
            return "redirect:/users/create";
        }
        if (StringUtils.isBlank(user.getPassword())) {
            ra.addFlashAttribute("errorMessage", "field password can't be empty");
            return "redirect:/users/create";
        }
        User userExists = userService.findUserByUsername(user.getUsername());
        if (userExists != null) {
            ra.addFlashAttribute("errorMessage", "There is already a user registered with the username provided");
            return "redirect:/users/create";
        }
        userService.saveOrUpdateUser(user);
        return "redirect:/users/list";
    }

    @PostMapping("/update")
    public String updateUser(@Valid UserData user, RedirectAttributes ra) {
        User userExists = userService.findUserByUsername(user.getUsername());
        if (userExists != null) {
            if (ObjectUtils.notEqual(user.getId(), userExists.getId())) {
                ra.addFlashAttribute("errorMessage", "There is already a user registered with the username provided");
                return "redirect:/users/update/" + user.getId();
            }
        }
        String result = processOrganisationCode(user, ra, userExists);
        if (result != null) {
        	return result;
        }
        userService.saveOrUpdateUser(user);
        return "redirect:/users/list";
    }

	private String processOrganisationCode(UserData user, RedirectAttributes ra, User userExists) {
		String organisationCode = user.getOrganisationCode();
		if (StringUtils.isNotBlank(organisationCode)) {
			organisationCode = StringUtils.trim(organisationCode);
        	Organisation organisation = organisationService.findOrganisationByCode(organisationCode);
        	if (organisation == null) {
                ra.addFlashAttribute("errorMessage", "No organisation is found by code "+organisationCode);
                return "redirect:/users/update/" + user.getId();
        	}
        	
        	userExists.setOrganisation(organisation);
        }
		return null;
	}
}
