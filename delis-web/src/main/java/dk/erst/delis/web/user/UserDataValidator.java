package dk.erst.delis.web.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserDataValidator implements Validator {

	private static UserPasswordValidator PASSWORD_VALIDATOR = new UserPasswordValidator();

	@Override
	public boolean supports(Class<?> clazz) {
		return UserData.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserData user = (UserData)target;
		
		if (user.isNew() || StringUtils.isNotBlank(user.getPassword()) || StringUtils.isNotBlank(user.getPassword())) {
			PASSWORD_VALIDATOR.isValid(user.getPassword(), errors);
			
			if (!user.getPassword().equals(user.getPassword2())) {
				errors.rejectValue("password2", "user.password.confirmation.equal");
			}
		}
		
	}

}
