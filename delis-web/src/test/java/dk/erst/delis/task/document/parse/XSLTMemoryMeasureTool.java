package dk.erst.delis.task.document.parse;

import dk.erst.delis.task.document.parse.cachingtransformerfactory.DelisTransformerFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XSLTMemoryMeasureTool {

	public static void main(String[] args) throws Exception {
		XSLTMemoryMeasureTool t = new XSLTMemoryMeasureTool();
		t.test();
	}

	public void test() throws Exception {
		Path baseDir = Paths.get("delis-resources");
		int creationTimes = 10;
		Files.walk(baseDir)
				.filter(p -> p.toString().endsWith(".xslt"))
				.forEach(path -> {
					testTransformerCreation(path.toAbsolutePath(), creationTimes);
				});
	}

	private void testTransformerCreation(Path xslFilePath, int creationTimes) {
		boolean withCache = true;
		System.gc();
		long usedMemoryBefore = getUsedMemory();
		System.out.println("Run with cache = " + withCache + " on " + xslFilePath);
		System.out.println("Used Memory before: " + formatMemory(usedMemoryBefore));

		try {
			TransformerFactory transformerFactory = DelisTransformerFactory.newInstance(withCache);
			long start = System.currentTimeMillis();
			for (int i = 0; i < creationTimes; i++) {
                long startCase = System.currentTimeMillis();
                Transformer transformer = XSLTUtil.buildTransformer(new FileInputStream(xslFilePath.toFile()), xslFilePath.toAbsolutePath(), transformerFactory);
                System.out.println("Case " + i + " " + (System.currentTimeMillis() - startCase) + " ms, " + formatMemory(getUsedMemory()));
            }
			long finish = System.currentTimeMillis();
			long usedMemoryAfter = getUsedMemory();
			System.out.println(xslFilePath.getFileName());
			System.out.println("Time spent: " + (finish - start));
			System.out.println("Memory used: " +  formatMemory(usedMemoryAfter - usedMemoryBefore));
			System.out.println("===================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String formatMemory(long s) {
		return String.format("%.2f", (s) / 1024.0 / 1024.0) + " mb";
	}

	private long getUsedMemory() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
