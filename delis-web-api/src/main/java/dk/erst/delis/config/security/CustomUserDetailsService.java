package dk.erst.delis.config.security;

import dk.erst.delis.config.web.security.UserStatusService;
import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
	private final UserStatusService userStatusService; 
    

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, UserStatusService userStatusService) {
        this.userRepository = userRepository;
		this.userStatusService = userStatusService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(login);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(login + " not found");
        }
        List<GrantedAuthority> authorities;
        String role;
        if (user.getOrganisation() == null) {
            role = Role.ROLE_ADMIN.name();
            authorities = AuthorityUtils.createAuthorityList(role);
        } else {
            role = Role.ROLE_USER.name();
            authorities = AuthorityUtils.createAuthorityList(role);
        }
        String organisation = user.getOrganisation() != null ? user.getOrganisation().getName() : null;
        return new CustomUserDetails(user, 
        		
				userStatusService.isEnabled(user), userStatusService.isAccountNonExpired(user),

				userStatusService.isCredentialsNonExpired(user), userStatusService.isAccountNonLocked(user),
        		
        		authorities, organisation, role);
    }

    private enum Role {
        ROLE_ADMIN, ROLE_USER
    }
    
    public void successfulLogin(String userLogin) {
		userRepository.resetInvalidLoginCountAndLogin(userLogin.toLowerCase(), Calendar.getInstance().getTime());

	}

	public void badCredentials(String userLogin) {
		userRepository.updateInvalidLoginCount(userLogin.toLowerCase(), Calendar.getInstance().getTime());
	}    
}
