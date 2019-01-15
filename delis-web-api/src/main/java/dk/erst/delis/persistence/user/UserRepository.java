package dk.erst.delis.persistence.user;

import dk.erst.delis.data.entities.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author funtusthan, created by 12.01.19
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameIgnoreCase(String username);
}
