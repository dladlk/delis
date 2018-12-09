package dk.erst.delis;

import java.io.InputStream;

public class TestUtil {
	
	public static InputStream getResourceByClass(Class<?> cls, String suffix) {
		return cls.getResourceAsStream(cls.getSimpleName()+"_"+suffix);
	}
	
}
