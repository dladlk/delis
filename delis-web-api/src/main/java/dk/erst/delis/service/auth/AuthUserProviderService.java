package dk.erst.delis.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author funtusthan, created by 12.01.19
 */

@Component
@Scope(value = "session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthUserProviderService {

    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AuthUserProviderService(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String getToken() {
        return (String) httpServletRequest.getAttribute("token");
    }
}
