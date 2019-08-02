package dk.erst.delis.data.listeners;

import dk.erst.delis.data.entities.user.User;

import org.springframework.util.StringUtils;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class FullNameGenerationListener {

    @PostLoad
    @PostPersist
    @PostUpdate
    public void generateFullName(User user){
        if (StringUtils.isEmpty(user.getFirstName()) || StringUtils.isEmpty(user.getLastName())){
            user.setFullName(null);
            return;
        }
        user.setFullName(user.getFirstName() + " " + user.getLastName());
    }
}
