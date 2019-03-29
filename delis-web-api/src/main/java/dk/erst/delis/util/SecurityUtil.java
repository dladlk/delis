package dk.erst.delis.util;

import dk.erst.delis.config.security.CustomUserDetails;

import lombok.experimental.UtilityClass;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author funtusthan, created by 22.03.19
 */

@UtilityClass
public class SecurityUtil {

    public Long getUserId() {
        return ((CustomUserDetails) SecurityUtil.getAuthentication().getPrincipal()).getId();
    }

    public String getUsername() {
        return SecurityUtil.getAuthentication().getName();
    }

    public boolean hasRole(Authentication authentication, String role) {
        if (Objects.nonNull(authentication)) {
            AtomicBoolean result = new AtomicBoolean(false);
            authentication.getAuthorities().forEach(authority -> result.set(authority.getAuthority().equalsIgnoreCase(role)));
            return result.get();
        }
        return false;
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
