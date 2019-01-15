package dk.erst.delis.service.auth;

import dk.erst.delis.rest.data.request.login.LoginData;

/**
 * @author funtusthan, created by 12.01.19
 */

public interface AuthService {

    String login(LoginData data);
    void logout(String token);
}
