package dk.erst.delis.web.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Getter
@Setter
class UserData {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
