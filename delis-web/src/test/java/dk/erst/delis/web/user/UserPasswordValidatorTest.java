package dk.erst.delis.web.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

public class UserPasswordValidatorTest {

	@Test
	public void testIsValid() {
		UserPasswordValidator v = new UserPasswordValidator();

		Map<?, ?> map = new HashMap<String, String>();
		BindingResult errors = new MapBindingResult(map, "user");
		assertFalse(v.isValid("12345", errors));
		assertTrue(errors.hasErrors());
		List<FieldError> fieldErrors = errors.getFieldErrors("password");
		assertEquals(10, fieldErrors.size());

		assertTrue(v.isValid("Systest1_", errors));
	}

}
