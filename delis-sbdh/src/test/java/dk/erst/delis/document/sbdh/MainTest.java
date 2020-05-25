package dk.erst.delis.document.sbdh;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;

public class MainTest {

	private static final Logger log = LoggerFactory.getLogger(MainTest.class);

	//@Test
	public void testMain() throws Exception {
		File resourcesFolder = new File("../delis-resources/examples/xml");
		String suffix = "_sbdh.xml";

		Main main = new Main();
		
		for (File sourceFile : resourcesFolder.listFiles()) {
			File targetFile = File.createTempFile(sourceFile.getName() + "_", ".xml");
			log.info("Copy " + sourceFile + " to " + targetFile);
			Files.copy(sourceFile, targetFile);
			targetFile.deleteOnExit();

			main.entryPoint(new String[] { targetFile.getAbsolutePath() });
			File sbdhFile = new File(targetFile.getAbsolutePath() + suffix);
			
			/*
			 * Important - SbdReader does not close given InputStream - it should be closed explicitely!
			 */			
			try (InputStream is = new FileInputStream(sbdhFile)){
				log.info("Check presence of result file " + sbdhFile);

				assertTrue("File " + sbdhFile + " does not exist", sbdhFile.exists());

				SbdReader reader = SbdReader.newInstance(is);
				Header actual = reader.getHeader();
				log.info("SBDH: " + actual);
				assertNotNull(actual);
			} finally {
				sbdhFile.delete();
			}
		}

	}

}
