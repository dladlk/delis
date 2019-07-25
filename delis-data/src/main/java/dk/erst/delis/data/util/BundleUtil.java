package dk.erst.delis.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import dk.erst.delis.data.enums.Named;

public class BundleUtil {

	private static final ResourceBundle RESOURCE = getBundle("messages.properties");
	private static final ResourceBundle RESOURCE_DA = getBundle("messages_da.properties");

	/*
	 * By some unexpected reason, ResourceBundle loaded properties file as UTF-8 encoding on Windows 10 JDK 
	 * 1.8.0_212, but according to javadoc of PropertyResourceBundle, it should load it as ISO-8859-1.
	 * 
	 * More-over, in Java 9 it is going to be used UTF-8 by default.
	 * 
	 * To avoid such unexpected behaviour, let's fix encoding to ISO-8859-1.
	 */
	private static ResourceBundle getBundle(String name) {
		InputStream inputStream = BundleUtil.class.getClassLoader().getResourceAsStream(name);
		try {
			return new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read resource " + name, e);
		}
	}

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
