package dk.erst.delis.data.util;

import dk.erst.delis.data.enums.Named;
import lombok.experimental.UtilityClass;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@UtilityClass
public class BundleUtil {

	private final ResourceBundle RESOURCE = ResourceBundle.getBundle("messages");

	public String getName(Named n) {
		String m;
		if (n instanceof Enum<?>) {
			Enum<?> e = (Enum<?>) n;
			m = BundleUtil.getMessage(e.getClass().getSimpleName() + "." + e.name());
			if (m == null) {
				m = e.name();
			}
		} else {
			m = BundleUtil.getMessage(n.getClass().getSimpleName() + ".name");
			if (m == null) {
				m = n.getClass().getSimpleName();
			}
		}
		return m;
	}

	private String getMessage(String name) {
		try {
			return RESOURCE.getString(name);
		} catch (MissingResourceException e) {
			return null;
		}
	}
}
