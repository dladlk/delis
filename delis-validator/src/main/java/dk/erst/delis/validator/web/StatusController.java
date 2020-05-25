package dk.erst.delis.validator.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dk.erst.delis.validator.service.ValidateStatBean;

@Controller
public class StatusController {

	@Autowired
	private ValidateStatBean validateStatusBean;

	@RequestMapping(value = "/status", produces = "text/plain")
	@ResponseBody()
	public String status() {
		return String.valueOf(validateStatusBean);
	}
}
