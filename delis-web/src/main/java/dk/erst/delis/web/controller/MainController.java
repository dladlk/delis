package dk.erst.delis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@RequestMapping("/")
	public String root() {
		return "redirect:/home";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

}
