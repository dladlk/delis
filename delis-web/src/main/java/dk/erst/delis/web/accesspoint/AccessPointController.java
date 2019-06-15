package dk.erst.delis.web.accesspoint;

import java.security.cert.CertificateException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.erst.delis.data.entities.access.AccessPoint;

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
    public String createNew(@Valid AccessPointData accessPoint, Model model) throws Exception {

        try {
            service.saveAccessPoint(accessPoint);
        } catch (CertificateException e) {
            model.addAttribute("errorMessage", "Can not build certificate with value provided");
            model.addAttribute("accessPoint", accessPoint);
            return "accesspoint/edit";
        }
        return "redirect:/accesspoint/list";
    }

    @GetMapping("create/{id}")
    public String createNew(@PathVariable(required = false) Long id, Model model) {
        if (id == null || id == 0) {
            model.addAttribute("accessPoint", new AccessPointData());
        } else {
            AccessPoint accessPoint = service.findById(id);
            AccessPointData accessPointData = new AccessPointData();
            service.copyToDTOEdit(accessPoint, accessPointData);
            model.addAttribute("accessPoint", accessPointData);
        }
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
    public String deleteUser(@PathVariable long id, Model model) {
        service.deleteAccessPoint(id);
        model.addAttribute("accessPointList", service.getAccessPoints());
        return "accesspoint/list";
    }
}
