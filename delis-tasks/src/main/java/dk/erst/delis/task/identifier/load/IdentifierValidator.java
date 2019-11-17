package dk.erst.delis.task.identifier.load;

import dk.erst.delis.data.enums.identifier.IdentifierValueType;

public class IdentifierValidator {

	public static boolean validateCVR(String cvr) {
		boolean valid = false;
		try {
			if (cvr.length() == 10 && cvr.toUpperCase().startsWith("DK")) {
				cvr = cvr.substring(2);
			}
			if (cvr.length() == 8) {
				Integer.parseInt(cvr);
				int sum = 0;
				for (int i = 0; i < cvr.length(); i++) {
					int ciffer = Integer.parseInt(String.valueOf(cvr.charAt(i)));
					if (i == 0) {
						sum = ciffer * 2;
					} else {
						int delSum = ciffer * (8 - i);
						sum += delSum;
					}
				}
				if (sum % 11 == 0) {
					valid = true;
				}
			}
		} catch (Exception ex) {
		}
		return valid;
	}

	public static boolean validateGLN(String gln) {
		boolean valid = false;
		try {
			if (gln.length() == 13) {
				Long.parseLong(gln);
				int sum = 0;
				for (int i = 0; i < gln.length() - 1; i++) {
					int ciffer = Integer.parseInt(String.valueOf(gln.charAt(i)));
					if (i % 2 == 0) {
						sum += ciffer * 1;
					} else {
						sum += ciffer * 3;
					}
				}
				int controlDigitActual = Integer.parseInt(String.valueOf(gln.charAt(12)));
				int controlDigitExpected = 10 - (sum % 10);
				if (controlDigitExpected == 10) {
					controlDigitExpected = 0;
				}
				if (controlDigitActual == controlDigitExpected) {
					valid = true;
				}
			}
		} catch (Exception ex) {
		}
		return valid;
	}

	public static boolean isValid(IdentifierValueType identifierType, String value) {
		if (identifierType == null) {
			return false;
		}
		switch (identifierType) {
		case DK_CVR:
			return validateCVR(value);
		case GLN:
			return validateGLN(value);
		default:
			return false;
		}
	}
}
