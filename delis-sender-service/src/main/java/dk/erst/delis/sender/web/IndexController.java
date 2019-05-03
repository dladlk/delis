package dk.erst.delis.sender.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	@RequestMapping(value = "/status", produces = "text/plain")
	@ResponseBody
	public String index() {
		StringBuilder sb = new StringBuilder();
		sb.append("DELIS send service");
		sb.append("\r\n");
		sb.append("Status: OK");
		String result = sb.toString();
		return result;
	}
}
