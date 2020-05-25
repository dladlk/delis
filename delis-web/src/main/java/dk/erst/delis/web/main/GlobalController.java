package dk.erst.delis.web.main;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import dk.erst.delis.config.web.security.CustomUserDetails;
import dk.erst.delis.data.enums.user.RoleType;

@ControllerAdvice
public class GlobalController {

	public static final SimpleGrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(RoleType.ADMIN.name());
	public static final SimpleGrantedAuthority USER_AUTHORITY = new SimpleGrantedAuthority(RoleType.USER.name());

	@ModelAttribute(name = "loggedIn")
	public boolean isLoggedIn(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated();
	}

	@ModelAttribute(name = "currentUserId")
	public Long getCurrentUserId(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CustomUserDetails) {
				CustomUserDetails cud = (CustomUserDetails) principal;
				return cud.getId();
			}
		}
		return null;
	}

	@ModelAttribute(name = "admin")
	public boolean isSuperAdmin(Authentication authentication) {
		boolean loggedIn = isLoggedIn(authentication);
		if (loggedIn) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			if (authorities != null) {
				return authorities.contains(ADMIN_AUTHORITY);
			}
		}
		return false;
	}

}
