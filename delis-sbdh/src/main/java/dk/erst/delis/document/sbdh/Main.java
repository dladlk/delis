package dk.erst.delis.document.sbdh;

import no.difi.vefa.peppol.common.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
//	public static void main(String[] args) {
//		Main main = new Main();
//		main.entryPoint(args);
//	}
	
	public void entryPoint(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: <PATH_TO_SOURCE_FILE> <PARTY_ID>");
			System.exit(-1);
		}
		String sourcePath = args[0];
		String partyId = args[1];
		Path sourceFilePath = Paths.get(sourcePath);
		Path targetFilePath = Paths.get(sourcePath + "_sbdh.xml");
		Path metadataFilePath = Paths.get(sourceFilePath.toString() + "_metadata.xml");
		SBDHTranslator translator = new SBDHTranslator();
		log.info("Generating SBDH to "+targetFilePath);
		Header header = translator.addHeader(sourceFilePath, targetFilePath);
		if(header == null) {
			log.error("SBDH has not been created, unable to write metadata");
			System.exit(-1);
		}
		log.info("SBDH successfully generated.");
		log.info("Generating metadata to "+metadataFilePath);
		if(translator.writeMetadata(header, partyId, metadataFilePath)) {
			log.info("Metadata successfully generated.");
		}
	}
}
