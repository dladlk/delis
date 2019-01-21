package dk.erst.delis.rest.controller.auth;

import dk.erst.delis.rest.data.request.login.LoginData;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.auth.AuthData;
import dk.erst.delis.service.auth.AuthService;
import dk.erst.delis.service.auth.AuthUserProviderService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author funtusthan, created by 12.01.19
 */

@Slf4j
@RestController
@RequestMapping("/rest/security")
public class SecurityRestController {

    private final AuthService authService;
    private final AuthUserProviderService authUserProviderService;

    @Autowired
    public SecurityRestController(AuthService authService, AuthUserProviderService authUserProviderService) {
        this.authService = authService;
        this.authUserProviderService = authUserProviderService;
    }

    @PostMapping("/signin")
    public ResponseEntity<DataContainer<AuthData>> login(@RequestBody @Valid LoginData loginData) {
        return ResponseEntity.ok(new DataContainer<>(authService.login(loginData)));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<DataContainer<Boolean>> logout() {
        authService.logout(authUserProviderService.getToken());
        return ResponseEntity.ok(new DataContainer<>(true));
    }
}
