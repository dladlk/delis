package dk.erst.delis.service.auth;

import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestUnauthorizedException;
import dk.erst.delis.persistence.user.UserRepository;
import dk.erst.delis.rest.data.request.login.LoginData;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author funtusthan, created by 12.01.19
 */

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String login(LoginData data) {
        User user = userRepository.findByUsernameIgnoreCase(data.getLogin());
        if (Objects.isNull(user)) {
            log.warn("UNAUTHORIZED user by login: " + data.getLogin());
            throw new RestUnauthorizedException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.UNAUTHORIZED.getReasonPhrase(), "invalid username or password")));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(data.getPassword(), user.getPassword())) {
            String token = UUID.randomUUID().toString();
            AuthTokenData authTokenData = AuthTokenMap.getAuthMap().get(user.getId());
            if (Objects.isNull(authTokenData)) {
                authTokenData = new AuthTokenData();
            }
            authTokenData.setToken(token);
            authTokenData.setExpired(new Date());
            AuthTokenMap.putAuthTokenData(user.getId(), authTokenData);
            return token;
        } else {
            log.warn("UNAUTHORIZED user by login: " + data.getLogin() + " and password: " + data.getPassword());
            throw new RestUnauthorizedException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.UNAUTHORIZED.getReasonPhrase(), "invalid username or password")));
        }
    }

    @Override
    public void logout(String token) {
        AuthTokenMap.getAuthMap()
                .values().removeIf(authTokenData -> Objects.equals(authTokenData.getToken(), token));
    }
}
