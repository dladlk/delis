package dk.erst.delis.document.sbdh;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: <PATH_TO_SOURCE_FILE>");
            System.exit(-1);
        }
        String sourcePath = args[0];
        String targetPath = new StringBuffer(sourcePath).insert(sourcePath.length() - 4, "_sbdh").toString();
        Path sourceFilePath = Paths.get(sourcePath);
        Path targetFilePath = Paths.get(targetPath);
        SBDHTranslator translator = new SBDHTranslator();
        translator.addHeader(sourceFilePath, targetFilePath);
    }
}
