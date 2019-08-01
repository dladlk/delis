package dk.erst.delis.rest.data.response.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthData {

    private String role;
    private String username;
    private String firstName;
    private String lastName;
    private String organisation;
    private String accessToken;
    private String refreshToken;
    private long lastLoginTime;
    private boolean disabledIrForm;
    
}
