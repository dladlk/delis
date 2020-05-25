package dk.erst.delis.web.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.passay.WhitespaceRule;
import org.springframework.validation.Errors;

public class UserPasswordValidator {

	private PasswordValidator validator;

	public UserPasswordValidator() {
		PropertiesMessageResolver propertiesMessageResolver = new PropertiesMessageResolver(getDefaultProperties());

		validator = new PasswordValidator(

				propertiesMessageResolver,

				new LengthRule(8, 20),

				new CharacterRule(EnglishCharacterData.UpperCase, 1),

				new CharacterRule(EnglishCharacterData.LowerCase, 1),

				new CharacterRule(EnglishCharacterData.Digit, 1),

				new CharacterRule(EnglishCharacterData.Special, 1),

				new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),

				new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),

				new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false),

				new WhitespaceRule()

		);
	}

	public boolean isValid(String password, Errors errors) {

		RuleResult result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			return true;
		}

		List<RuleResultDetail> details = result.getDetails();
		for (RuleResultDetail ruleResultDetail : details) {
			String resolvedMessage = validator.getMessageResolver().resolve(ruleResultDetail);
			errors.rejectValue("password", "", resolvedMessage);
		}
		return false;
	}

	public static Properties getDefaultProperties() {
		final Properties props = new Properties();
		InputStream in = null;
		try {
			in = UserPasswordValidator.class.getResourceAsStream("/password_error_messages.properties");
			props.load(in);
		} catch (Exception e) {
			throw new IllegalStateException("Error loading default message properties.", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
}
