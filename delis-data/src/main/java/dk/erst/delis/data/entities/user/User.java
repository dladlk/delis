package dk.erst.delis.data.entities.user;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.listeners.FullNameGenerationListener;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Getter
@Setter
@Entity
@Table(name = "user")
@EntityListeners({FullNameGenerationListener.class})
public class User extends AbstractCreateUpdateEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Email
    @Column(unique = true)
    private String email;

    @Transient
    private String fullName;
}
