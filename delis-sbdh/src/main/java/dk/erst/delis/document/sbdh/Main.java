package dk.erst.delis.document.sbdh;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <PATH_TO_SOURCE_FILE>");
			System.exit(-1);
		}
		String sourcePath = args[0];
		String targetPath = sourcePath + "_sbdh.xml";
		Path sourceFilePath = Paths.get(sourcePath);
		Path targetFilePath = Paths.get(targetPath);
		SBDHTranslator translator = new SBDHTranslator();
		
		log.info("Generating SBDH to "+targetFilePath);
		translator.addHeader(sourceFilePath, targetFilePath);
	}
}
