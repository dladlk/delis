package dk.erst.delis.config.web.security;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Getter
@Setter
class CustomUserDetails extends User {

    private Long id;

    CustomUserDetails(dk.erst.delis.data.user.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.id = user.getId();
    }
}
