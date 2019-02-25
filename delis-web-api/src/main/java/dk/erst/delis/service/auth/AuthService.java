package dk.erst.delis.service.auth;

import dk.erst.delis.rest.data.request.login.LoginData;
import dk.erst.delis.rest.data.response.auth.AuthData;

/**
 * @author funtusthan, created by 12.01.19
 */

public interface AuthService {

    AuthData login(LoginData data);
    void logout(String token);
}
