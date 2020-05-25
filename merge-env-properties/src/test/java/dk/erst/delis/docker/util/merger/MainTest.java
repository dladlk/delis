package dk.erst.delis.docker.util.merger;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() throws IOException {
		String testPath = "src/test/resources/fs-plugin.properties";
		File tempFile = File.createTempFile("dk.erst.delis.docker.util.merger.MainTest", ".properties");
		try {
			try (FileInputStream is = new FileInputStream(new File(testPath)); FileOutputStream fos = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[10 * 1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
			}
			String prefix = "FS_PROPERTIES_";
			Map<String, String> map = new HashMap<String, String>();
			map.put(prefix + "fsplugin_authentication_password", "Systest1_");
			map.put(prefix + "fsplugin_authentication_user", "fsplugin");
			assertEquals(2, Main.main(prefix, tempFile.getAbsolutePath(), map));
		} finally {
			if (tempFile.delete()) {
				System.out.println("Test file is deleted: " + tempFile);
			}
		}
	}

}
