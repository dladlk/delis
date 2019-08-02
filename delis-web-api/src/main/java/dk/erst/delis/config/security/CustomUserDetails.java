package dk.erst.delis.config.security;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import dk.erst.delis.rest.data.response.auth.AuthData;

import java.util.Collection;
import java.util.Date;

@Getter
public class CustomUserDetails extends User {

    private static final long serialVersionUID = 7085909667165639569L;

    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String organisation;
    private String role;
    private Date lastLoginTime;
    private boolean disabledIrForm;

    CustomUserDetails(dk.erst.delis.data.entities.user.User user, Collection<? extends GrantedAuthority> authorities, String organisation, String role) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.id = user.getId();
        this.userName = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.organisation = organisation;
        this.role = role;
        this.lastLoginTime = new Date();
        this.disabledIrForm = user.getDisabledIrForm() != null && user.getDisabledIrForm().booleanValue();
    }
    
    public AuthData buildAuthData () {
    	
    	 AuthData authData = new AuthData();
    	 
         authData.setRole(this.getRole());
         authData.setUsername(this.getUserName());
         authData.setFirstName(this.getFirstName());
         authData.setLastName(this.getLastName());
         authData.setOrganisation(this.getOrganisation());
         authData.setLastLoginTime(this.getLastLoginTime().getTime());
         authData.setDisabledIrForm(this.isDisabledIrForm());
         
         return authData;
    }
}
