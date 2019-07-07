package dk.erst.delis.rest.data.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthData {

    private String role;
    private String username;
    private String accessToken;
    private String refreshToken;
}
