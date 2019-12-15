package dk.erst.delis.web.user;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dk.erst.delis.web.user.PasswordController.PasswordForm;

public class PasswordFormValidator implements Validator {

	private static UserPasswordValidator PASSWORD_VALIDATOR = new UserPasswordValidator();

	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordForm user = (PasswordForm) target;

		PASSWORD_VALIDATOR.isValid(user.getPassword(), errors);

		if (!user.getPassword().equals(user.getPassword2())) {
			errors.rejectValue("password2", "user.password.confirmation.equal");
		}

	}

}
