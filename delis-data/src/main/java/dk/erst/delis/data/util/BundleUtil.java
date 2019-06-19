package dk.erst.delis.data.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import dk.erst.delis.data.enums.Named;

public class BundleUtil {

	private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("messages");
	private static final ResourceBundle RESOURCE_DA = ResourceBundle.getBundle("messages", Locale.forLanguageTag("da"));

	public static String getName(Named n) {
		return getName(RESOURCE, n);
	}

	public static String getNameDa(Named n) {
		return getName(RESOURCE_DA, n);
	}
	
	private static String getName(ResourceBundle bundle, Named n) {
		String m;
		if (n instanceof Enum<?>) {
			Enum<?> e = (Enum<?>) n;
			m = BundleUtil.getMessage(bundle, e.getClass().getSimpleName() + "." + e.name());
			if (m == null) {
				m = e.name();
			}
		} else {
			m = BundleUtil.getMessage(bundle, n.getClass().getSimpleName() + ".name");
			if (m == null) {
				m = n.getClass().getSimpleName();
			}
		}
		return m;
	}

	private static String getMessage(ResourceBundle bundle, String name) {
		try {
			return bundle.getString(name);
		} catch (MissingResourceException e) {
			return null;
		}
	}
}
