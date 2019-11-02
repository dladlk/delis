package dk.erst.delis.task.email;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmailValidatorTest {

	@Test
	public void testIsValidEmail() {
		assertTrue(EmailValidator.isValidEmail("user@server.dk"));
		assertTrue(EmailValidator.isValidEmail("user@127.0.0.1"));
		assertTrue(EmailValidator.isValidEmail("user-user.user@127.0.0.1"));
		assertFalse(EmailValidator.isValidEmail("user-user"));
		assertFalse(EmailValidator.isValidEmail(null));
		assertFalse(EmailValidator.isValidEmail("   "));
	}

}
