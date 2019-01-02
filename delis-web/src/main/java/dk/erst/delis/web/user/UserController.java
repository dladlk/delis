package dk.erst.delis.web.user;

import dk.erst.delis.data.user.User;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("create")
    public String createNewUser(Model model) {
        model.addAttribute("user", new User());
        return "user/edit";
    }

    @PostMapping("/create")
    public String createNewUser(@Valid User user, BindingResult bindingResult) {

        User userExists = userService.findUserByUsername(user.getUsername());
        if (userExists != null) {
            bindingResult
                    .rejectValue("username", "error.user",
                            "There is already a user registered with the username provided");
        }
        if (bindingResult.hasErrors()) {
            return "redirect:/users/create";
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            userExists = userService.findByEmail(user.getEmail());
            if (userExists != null) {
                bindingResult
                        .rejectValue("email", "error.user",
                                "There is already a user registered with the email provided");
            }
            if (bindingResult.hasErrors()) {
                return "redirect:/users/create";
            }
        }

        userService.saveUser(user);
        return "redirect:/users/list";
    }
}
