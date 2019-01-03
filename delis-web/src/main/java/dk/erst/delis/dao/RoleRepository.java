package dk.erst.delis.dao;

import dk.erst.delis.data.user.Role;
import dk.erst.delis.data.user.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRole(RoleType role);
}
