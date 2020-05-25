package dk.erst.delis.config.web.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import dk.erst.delis.data.entities.organisation.Organisation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails extends User {

	private static final long serialVersionUID = 2301527384837617892L;

	private Long id;

	private String fullName;

	private Organisation organisation;

	CustomUserDetails(dk.erst.delis.data.entities.user.User user, 
			boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, 
			Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(),
				enabled,
				accountNonExpired, credentialsNonExpired,
				accountNonLocked,
				authorities);
		this.fullName = user.getFirstName() + " " + user.getLastName();
		this.id = user.getId();
		this.organisation = user.getOrganisation();
	}

	public long getOrganisationId() {
		return this.organisation != null ? this.organisation.getId() : -1;
	}

}
