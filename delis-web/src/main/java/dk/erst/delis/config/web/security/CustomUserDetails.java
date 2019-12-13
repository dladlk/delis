package dk.erst.delis.config.web.security;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import dk.erst.delis.data.entities.organisation.Organisation;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {

	private static final long serialVersionUID = 2301527384837617892L;

	private Long id;

	private String fullName;
	
	private Organisation organisation;
	
	private boolean disabled;

	CustomUserDetails(dk.erst.delis.data.entities.user.User user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
		this.fullName = user.getFirstName() + " " + user.getLastName();
		this.id = user.getId();
		this.organisation = user.getOrganisation();
		this.disabled = user.isDisabled();
	}

	public long getOrganisationId() {
		return this.organisation != null ? this.organisation.getId() : -1;
	}
}
