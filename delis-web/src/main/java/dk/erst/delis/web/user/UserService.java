package dk.erst.delis.web.user;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.task.organisation.OrganisationService;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganisationService organisationService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public User findUserByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findById(Long id) {
		return findOne(id);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public boolean validate(UserData user, BindingResult bindingResult) {
		User userExists = this.findUserByUsername(user.getUsername());
		if (userExists != null && !userExists.getId().equals(user.getId())) {
			bindingResult.rejectValue("username", "user.username.duplicated");
		}

		String organisationCode = user.getOrganisationCode();
		if (StringUtils.isNotBlank(organisationCode)) {
			organisationCode = StringUtils.trim(organisationCode);
			Organisation organisation = organisationService.findOrganisationByCode(organisationCode);
			if (organisation == null) {
				bindingResult.rejectValue("organisationCode", "user.organisationCode.notfound");
			}
		}
		return !bindingResult.hasErrors();
	}

	public void saveOrUpdateUser(UserData userData) {
		User user;
		if (userData.getId() == null) {
			user = new User();
		} else {
			user = findById(userData.getId());
		}
		if (StringUtils.isNotBlank(userData.getUsername())) {
			user.setUsername(userData.getUsername());
		}
		if (StringUtils.isNotBlank(userData.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(userData.getPassword()));
		}
		if (StringUtils.isNotBlank(userData.getEmail())) {
			user.setEmail(userData.getEmail());
		}

		user.setUsername(userData.getUsername());
		user.setLastName(userData.getLastName());
		user.setFirstName(userData.getFirstName());

		if (userData.isDisabledIrForm()) {
			user.setDisabledIrForm(Boolean.TRUE);
		} else {
			user.setDisabledIrForm(Boolean.FALSE);
		}
		
		Organisation org = null;
		if (userData.getOrganisationCode() != null) {
			org = organisationService.findOrganisationByCode(userData.getOrganisationCode());
		}
		user.setOrganisation(org);

		userRepository.save(user);
	}

	public boolean deactivateUser(Long id) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			if (!user.isDisabled()) {
				user.setDisabled(true);
				userRepository.save(user);
				return true;
			}
		}
		return false;
	}

	public boolean activateUser(Long id) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			if (user.isDisabled()) {
				user.setDisabled(false);
				userRepository.save(user);
				return true;
			}
		}
		return false;
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
