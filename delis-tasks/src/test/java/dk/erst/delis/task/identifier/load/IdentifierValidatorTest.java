package dk.erst.delis.task.identifier.load;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentifierValidatorTest {

	@Test
	public void testValidateCVR() {
		assertTrue(IdentifierValidator.validateCVR("DK00000124"));
		assertTrue(IdentifierValidator.validateCVR("00000124"));
		assertFalse(IdentifierValidator.validateCVR("00000125"));
		assertFalse(IdentifierValidator.validateCVR(" 00000125 "));
		assertFalse(IdentifierValidator.validateCVR(null));
		assertFalse(IdentifierValidator.validateCVR(""));
		assertFalse(IdentifierValidator.validateCVR("DK"));
	}

	@Test
	public void testValidateEAN13() {
		assertTrue(IdentifierValidator.validateGLN("0000000000017"));
		assertFalse(IdentifierValidator.validateGLN("0000000000018"));
		assertFalse(IdentifierValidator.validateGLN(null));
		assertFalse(IdentifierValidator.validateGLN(""));
		assertFalse(IdentifierValidator.validateGLN(" 0000000000017 "));
	}

}
