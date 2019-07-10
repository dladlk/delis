package dk.erst.delis.rest.data.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthData {

    private String role;
    private String username;
    private String firstName;
    private String lastName;
    private String organisation;
    private String accessToken;
    private String refreshToken;
    private Date lastLoginTime;
}
