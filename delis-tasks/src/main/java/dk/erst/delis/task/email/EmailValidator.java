package dk.erst.delis.task.email;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class EmailValidator {

	private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
	private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	private static final String EMAIL_PATTERN_STR = "^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$";

	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STR);

	public static boolean isValidEmail(String email) {
		return email != null && StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
	}
}
