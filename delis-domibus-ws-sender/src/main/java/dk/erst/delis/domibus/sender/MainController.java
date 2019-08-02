package dk.erst.delis.domibus.sender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Value("${config.wsSendParty}")
    private String wsSendParty;

    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("wsSendParty", wsSendParty);
        return "index";
    }
}
