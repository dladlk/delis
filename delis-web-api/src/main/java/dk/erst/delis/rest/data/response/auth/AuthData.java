package dk.erst.delis.rest.data.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author funtusthan, created by 21.01.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthData {

    private String username;
    private String accessToken;
    private String refreshToken;
}
