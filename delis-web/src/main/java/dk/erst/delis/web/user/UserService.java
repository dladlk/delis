package dk.erst.delis.web.user;

import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.user.User;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 02.01.19
 */

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    User findUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    User findById(Long id) {
        return findOne(id);
    }

    List<User> findAll() {
        return userRepository.findAll();
    }

    void saveUser(UserData userData) {
        User user;
        if (userData.getId() == null) {
            user = new User();
        } else {
            user = findById(userData.getId());
        }
        if (StringUtils.isNotBlank(userData.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userData.getPassword()));
        }

        user.setUsername(userData.getUsername());
        user.setLastName(userData.getLastName());
        user.setFirstName(userData.getFirstName());
        user.setEmail(userData.getEmail());
        userRepository.save(user);
    }

    void deleteUser(Long id) {
        userRepository.delete(findOne(id));
    }

    private User findOne(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return user;
        } else {
            throw new RuntimeException();
        }
    }
}
