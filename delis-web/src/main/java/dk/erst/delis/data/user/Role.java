package dk.erst.delis.data.user;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String role;
}
