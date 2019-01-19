package dk.erst.delis.rest.controller.content.access;

import dk.erst.delis.service.content.access.AccessPointService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

/**
 * @author funtusthan, created by 17.01.19
 */

@RestController
@RequestMapping("/rest/access-point")
public class AccessPointController {

    private final AccessPointService accessPointService;

    @Autowired
    public AccessPointController(AccessPointService accessPointService) {
        this.accessPointService = accessPointService;
    }

    @GetMapping
    public ResponseEntity getAll(WebRequest webRequest) {
        return ResponseEntity.ok(accessPointService.getAll(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getOneById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(accessPointService.getOneById(id));
    }
}
