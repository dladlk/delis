package dk.erst.delis.config.security;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private static final long serialVersionUID = 7085909667165639569L;

    private Long id;
    private String userName;

    CustomUserDetails(dk.erst.delis.data.entities.user.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.id = user.getId();
        this.userName = StringUtils.isNotEmpty(user.getFullName()) ? user.getFullName() : user.getUsername();
    }
}
