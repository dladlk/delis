package dk.erst.delis.web.accesspoint;

import java.security.cert.CertificateException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.task.organisation.setup.ValidationResultData;

@Controller
@RequestMapping("/accesspoint")
public class AccessPointController {

    private AccessPointService service;

    @Autowired
    public AccessPointController(AccessPointService service) {
        this.service = service;
    }

    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("accessPointList", service.getAccessPointDTOs());
        return "accesspoint/list";
    }

    @PostMapping("save")
    public String save(AccessPointData accessPoint, Model model) throws Exception {
    	ValidationResultData validationResultData = service.validate(accessPoint);
		if (!validationResultData.isAllValid()) {
			model.addAttribute("errorMessage", "Some fields are not valid");
			model.addAttribute("accessPoint", accessPoint);
			model.addAttribute("validation", validationResultData);
			return "accesspoint/edit";
		}
        try {
            service.saveAccessPoint(accessPoint);
        } catch (CertificateException e) {
            model.addAttribute("errorMessage", "Can not build certificate with value provided");
            model.addAttribute("accessPoint", accessPoint);
            return "accesspoint/edit";
        }
        return "redirect:/accesspoint/list";
    }

    @GetMapping("create")
    public String createNew(Model model) {
        model.addAttribute("accessPoint", new AccessPointData());
        return "accesspoint/edit";
    }
    
    @GetMapping("update/{id}")
    public String updateAccessPoint(@PathVariable long id, Model model) {
        AccessPoint accessPoint = service.findById(id);
        AccessPointData accessPointData = new AccessPointData();
        service.copyToDTOEdit(accessPoint, accessPointData);
        model.addAttribute("accessPoint", accessPointData);
        return "accesspoint/edit";
    }
    
	@GetMapping("delete/{id}")
	public String deleteAccessPoint(@PathVariable long id, RedirectAttributes ra) {
		List<Organisation> referenceList = service.getOrganisationReferencedAccessPointList(id);
		if (!referenceList.isEmpty()) {
			final StringBuilder sb = new StringBuilder();
			referenceList.forEach(o -> {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(o.getName());
			});
			ra.addFlashAttribute("errorMessage", "Access point is used in configuration setup of " + referenceList.size() + " organisation(s). Before deletion, please switch from this access point to another. Organisations: " + sb.toString());
		} else {
			service.deleteAccessPoint(id);
			ra.addFlashAttribute("message", "Access point is deleted");
		}
		return "redirect:/accesspoint/list";
	}
}
