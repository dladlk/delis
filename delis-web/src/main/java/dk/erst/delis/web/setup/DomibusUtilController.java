package dk.erst.delis.web.setup;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.domibus.util.XmlService;
import dk.erst.delis.domibus.util.pmode.PmodeData;
import dk.erst.delis.domibus.util.pmode.PmodeUtil;

@Controller
public class DomibusUtilController {
	
	@Autowired
	private XmlService xmlService; 

	@GetMapping("/setup/domibus")
	public String view() {
		return "/setup/domibus";
	}

	@PostMapping("/setup/domibus/pmode")
	public ResponseEntity<Object> generatePMode(@RequestParam String url, @RequestParam String partyName, Model model, RedirectAttributes ra) {
		PmodeData pmodeData = new PmodeData();
		String endpointUrl = url;
		pmodeData.setEndpointUrl(endpointUrl + "/services/msh");
		pmodeData.setPartyName(partyName);
		pmodeData = PmodeUtil.populateServicesActionsLegs(pmodeData);		
		
		String result = xmlService.build(pmodeData);
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pmode.xml\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(new InputStreamResource(new ByteArrayInputStream(result.getBytes())));
	}
	
}
