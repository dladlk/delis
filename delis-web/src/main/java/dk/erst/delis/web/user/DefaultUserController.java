package dk.erst.delis.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@RestController
@RequestMapping("/default/user")
public class DefaultUserController {

    private final UserService userService;

    @Autowired
    public DefaultUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> createDefaultUser() {
        UserData user = new UserData();
        user.setPassword("admin");
        user.setUsername("admin");
        userService.saveUser(user);
        return ResponseEntity.ok("ok");
    }
}
