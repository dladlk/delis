package dk.erst.delis.web.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

	@GetMapping("/task/index")
	public String index() {
		return "/task/index";
	}
	
	@GetMapping("/task/identifierPublish")
	public String startIdentifierPublishJob() {
		return "/task/index";
	}
}
