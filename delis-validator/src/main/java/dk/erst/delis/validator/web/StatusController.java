package dk.erst.delis.validator.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatusController {

	@RequestMapping(value = "/status", produces = "text/plain")
	@ResponseBody()
	public String status() {
		return "OK";
	}
}
