package dk.erst.delis.rest.controller.logout;

import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.service.security.TokenService;
import dk.erst.delis.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/logout")
public class RestLogoutController {

    private final ConsumerTokenServices consumerTokenServices;
    private final TokenService tokenService;

    @Autowired
    public RestLogoutController(ConsumerTokenServices consumerTokenServices, TokenService tokenService) {
        this.consumerTokenServices = consumerTokenServices;
        this.tokenService = tokenService;
    }

    @DeleteMapping
    public ResponseEntity<DataContainer<Boolean>> logout(HttpSession session) {
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityUtil.getAuthentication();
        OAuth2AccessToken token = ((DefaultTokenServices) consumerTokenServices).getAccessToken(authentication);
        if (token != null) {
        	tokenService.removeAccessToken(token);
        }
        session.invalidate();
        return ResponseEntity.ok(new DataContainer<>(true));
    }
}
