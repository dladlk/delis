package dk.erst.delis.config.web.security;

import dk.erst.delis.dao.UserRepository;

import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.data.enums.user.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameIgnoreCase(login);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(login + " not found");
        }

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(RoleType.ADMIN.name());

        return new CustomUserDetails(user, authorities);
    }
}
