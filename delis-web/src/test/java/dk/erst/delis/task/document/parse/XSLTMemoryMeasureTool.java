package dk.erst.delis.task.document.parse;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.TransformerFactory;

import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.parse.cachingtransformerfactory.DelisTransformerFactory;

public class XSLTMemoryMeasureTool {

	public static void main(String[] args) throws Exception {
		XSLTMemoryMeasureTool t = new XSLTMemoryMeasureTool();
		t.test();
	}

	public void test() throws Exception {
		Path path1 = Paths.get("../delis-resources/transformation/bis3_to_oioubl/v_2018-03-14_34841/BIS-Billing-INV_2_OIOUBL_INV.xslt");
		Path path2 = Paths.get("../delis-resources/validation/sch/bis3/cen_2018-03-15_1/CEN-EN16931-UBL.xslt");
//        testTransformerCreation(path1, 10);
		testTransformerCreation(path2, 10);

	}

	private void testTransformerCreation(Path xslFilePath, int creationTimes) throws Exception {
		boolean withCache = true;
		long usedMemoryBefore = getUsedMemory();

		System.out.println("Run with cache = " + withCache + " on " + xslFilePath);
		System.out.println("Used Memory before: " + formatMemory(usedMemoryBefore));

		TransformerFactory transformerFactory = DelisTransformerFactory.newInstance(withCache);

		long start = System.currentTimeMillis();
		for (int i = 0; i < creationTimes; i++) {
			long startCase = System.currentTimeMillis();
			XSLTUtil.buildTransformer(new FileInputStream(xslFilePath.toFile()), xslFilePath.toAbsolutePath(), transformerFactory);
			System.out.println("Case " + i + " " + (System.currentTimeMillis() - startCase) + " ms, " + formatMemory(getUsedMemory()));
		}
		long finish = System.currentTimeMillis();
		long usedMemoryAfter = getUsedMemory();
		System.out.println("Memory increased: " + formatMemory(usedMemoryAfter - usedMemoryBefore));
		System.out.println("Time spent: " + (finish - start));
	}

	private String formatMemory(long s) {
		return String.format("%.2f", (s) / 1024.0 / 1024.0) + " mb";
	}

	private long getUsedMemory() {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
