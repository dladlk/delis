package dk.erst.delis.task.document.parse;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import dk.erst.delis.TestUtil;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.rule.DefaultRuleBuilder;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.task.document.parse.cachingtransformerfactory.DelisTransformerFactory;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.StandardErrorListener;

@Slf4j
public class XSLTMemoryMeasureTool {

	public static void main(String[] args) throws Exception {
		XSLTMemoryMeasureTool t = new XSLTMemoryMeasureTool();
		t.test();
	}

	public void test() throws Exception {
		ConfigBean configBean = new ConfigBean(TestUtil.getEmptyConfigValueDaoRepository());

		List<Path> xsltPathList = new ArrayList<>();
		for (RuleDocumentTransformation r : DefaultRuleBuilder.buildDefaultTransformationRuleList()) {
			Path path = configBean.getStorageTransformationPath().resolve(r.getRootPath());
			xsltPathList.add(path);
		}
		for (RuleDocumentValidation r : DefaultRuleBuilder.buildDefaultValidationRuleList()) {
			if (r.getValidationType() == RuleDocumentValidationType.SCHEMATRON) {
				Path path = configBean.getStorageValidationPath().resolve(r.getRootPath());
				xsltPathList.add(path);
			}
		}

		int creationTimes = 10;

		TransformerFactory factory = (TransformerFactory) Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
		StandardErrorListener listener = new StandardErrorListener();
		listener.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);
		factory.setErrorListener(listener);

		
		
		for (Path p : xsltPathList) {
			List<Templates> templateList = new ArrayList<>();

			byte[] b = new byte[150 * 1024 * 1024];
			for (int i = 0; i < b.length; i += 1024) {
				b[i] = 1;
			}
			b = null;

			long usedMemory = getUsedMemory();
			log.info(formatMemory(usedMemory) + " " + p);
			for (int i = 0; i < creationTimes; i++) {
				String systemId = p.toFile().toString();
				templateList.add(factory.newTemplates(new StreamSource(new FileInputStream(p.toFile()), systemId)));
			}

			log.info(p.toFile().getName() + "=" + (formatMemory(getUsedMemory() - usedMemory)) + " " + p.toFile().length() + " = " + templateList.size());
			templateList.clear();
			templateList = null;
		}

		if (true) {
			return;
		}

		for (Path p : xsltPathList) {
			testTransformerCreation(p, creationTimes);
		}

		if (false) {
			Path baseDir = Paths.get("../delis-resources");
			Files.walk(baseDir).filter(p -> p.toString().endsWith(".xslt")).forEach(path -> {
				testTransformerCreation(path.toAbsolutePath(), creationTimes);
			});
		}
	}

	private void testTransformerCreation(Path xslFilePath, int creationTimes) {
		boolean withCache = true;
		System.gc();
		long usedMemoryBefore = getUsedMemory();
		log.info("Run with cache = " + withCache + " on " + xslFilePath);
		log.info("Used Memory before: " + formatMemory(usedMemoryBefore));

		try {
			TransformerFactory transformerFactory = DelisTransformerFactory.newInstance(withCache);
			long start = System.currentTimeMillis();
			for (int i = 0; i < creationTimes; i++) {
				long startCase = System.currentTimeMillis();
				Transformer transformer = XSLTUtil.buildTransformer(new FileInputStream(xslFilePath.toFile()), xslFilePath.toAbsolutePath(), transformerFactory);
				log.info("Case " + i + " " + (System.currentTimeMillis() - startCase) + " ms, " + formatMemory(getUsedMemory()));
			}
			long finish = System.currentTimeMillis();
			long usedMemoryAfter = getUsedMemory();
			log.info(xslFilePath.getFileName().toString());
			log.info("Time spent: " + (finish - start));
			log.info("Memory used: " + formatMemory(usedMemoryAfter - usedMemoryBefore));
			log.info("===================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
