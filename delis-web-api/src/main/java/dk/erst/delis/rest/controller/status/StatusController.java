package dk.erst.delis.rest.controller.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author funtusthan, created by 19.01.19
 */

@RestController
@RequestMapping("/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("delis-web-api status: OK");
    }
}
