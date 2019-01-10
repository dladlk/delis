package dk.erst.delis.web.user;

import dk.erst.delis.data.user.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

        List<User> users = userService.findAll();
        if (CollectionUtils.isNotEmpty(users)) {
            return ResponseEntity.ok("user is exist");
        }

        UserData user = new UserData();
        user.setPassword("admin");
        user.setUsername("admin");
        userService.saveOrUpdateUser(user);
        return ResponseEntity.ok("create new user successful");
    }
}
