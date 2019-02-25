package dk.erst.delis.dao;

import dk.erst.delis.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameIgnoreCase(String username);
    User findByEmail(String email);
}