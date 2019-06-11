package dk.erst.delis.config.web.security;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
class CustomUserDetails extends User {

	private static final long serialVersionUID = 2301527384837617892L;

	private Long id;

	private String fullName;

	CustomUserDetails(dk.erst.delis.data.entities.user.User user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
		this.fullName = user.getFirstName() + " " + user.getLastName();
		this.id = user.getId();
	}
}
