package dk.erst.delis.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.erst.delis.dao.RoleRepository;
import dk.erst.delis.data.user.Role;
import dk.erst.delis.data.user.RoleType;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@RestController
@RequestMapping("/default/user")
public class DefaultUserController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> createDefaultUser() {
        Role role = new Role();
        role.setRole(RoleType.ADMIN);
        roleRepository.save(role);
        UserData user = new UserData();
        user.setPassword("admin");
        user.setUsername("admin");
        userService.saveUser(user);
        return ResponseEntity.ok("ok");
    }
}
