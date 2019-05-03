package dk.erst.delis.config.security;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author funtusthan, created by 22.03.19
 */

@Getter
public class CustomUserDetails extends User {

    private Long id;
    private String userName;

    CustomUserDetails(dk.erst.delis.data.entities.user.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.id = user.getId();
        this.userName = StringUtils.isNotEmpty(user.getFullName()) ? user.getFullName() : user.getUsername();
    }
}
