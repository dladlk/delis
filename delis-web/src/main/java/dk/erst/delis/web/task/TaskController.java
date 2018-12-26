package dk.erst.delis.web.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

	@GetMapping("/task/index")
	public String index() {
		return "/task/index";
	}

	@GetMapping("/task/identifierLoad")
	public String identifierLoad(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/identifierPublish")
	public String identifierPublish(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/documentLoad")
	public String documentLoad(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/documentValidate")
	public String documentValidate(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/documentDeliver")
	public String documentDeliver(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/unimplemented")
	private String unimplemented(Model model) {
		model.addAttribute("errorMessage", "Not implemented");
		return "/task/index";
	}
}
