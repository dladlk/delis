package dk.erst.delis.web.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class UserData {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String organisationCode;
    private boolean disabledIrForm;
}
