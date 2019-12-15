package dk.erst.delis.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.entities.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsernameIgnoreCase(String username);

	User findByEmail(String email);

	@Transactional
	@Modifying
	@Query("UPDATE User set invalidLoginCount = COALESCE(invalidLoginCount, 0) + 1, lastInvalidLoginTime = ?2 where lower(username) = ?1 ")
	int updateInvalidLoginCount(String username, Date date);

	@Transactional
	@Modifying
	@Query("UPDATE User set invalidLoginCount = 0, lastInvalidLoginTime = null, lastLoginTime = ?2 where lower(username) = ?1 ")
	int resetInvalidLoginCountAndLogin(String userLogin, Date date);

	@Transactional
	@Modifying
	@Query("UPDATE User set invalidLoginCount = 0, lastInvalidLoginTime = null where lower(username) = ?1 ")
	int resetInvalidLoginCount(String userLogin, Date date);
}