package dk.erst.delis.email.condition;

public class SpringEmailPresentUtil {

	public static boolean present() {
		try {
			Class.forName("org.springframework.mail.javamail.JavaMailSender");
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
