package dk.erst.delis.task.document.parse;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.junit.Ignore;
import org.junit.Test;

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

	@Test
	@Ignore
	public void test() throws Exception {
		ConfigBean configBean = new ConfigBean(TestUtil.getEmptyConfigValueDaoRepository());

		List<Path> xsltPathList = new ArrayList<>();
		for (RuleDocumentTransformation rdf : DefaultRuleBuilder.buildDefaultTransformationRuleList()) {
			Path path = configBean.getStorageTransformationPath().resolve(rdf.getRootPath());
			xsltPathList.add(path);
		}
		for (RuleDocumentValidation rdv : DefaultRuleBuilder.buildDefaultValidationRuleList()) {
			if (rdv.getValidationType() == RuleDocumentValidationType.SCHEMATRON) {
				Path path = configBean.getStorageValidationPath().resolve(rdv.getRootPath());
				xsltPathList.add(path);
			}
		}

		int creationTimes = 10;

		TransformerFactory factory = TransformerFactory.newInstance();
		StandardErrorListener listener = new StandardErrorListener();
		listener.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);
		factory.setErrorListener(listener);

		for (Path xsltPath : xsltPathList) {
			List<Templates> templateList = new ArrayList<>();

			byte[] b = new byte[150 * 1024 * 1024];
			for (int i = 0; i < b.length; i += 1024) {
				b[i] = 1;
			}
			b = null;

			long usedMemoryBefore = getUsedMemory();
			log.info("Testing "+ xsltPath.getFileName()+"; file size = "+xsltPath.toFile().length());
			log.info("usedMemoryBefore = "+formatMemory(usedMemoryBefore));
			for (int i = 0; i < creationTimes; i++) {
				String systemId = xsltPath.toFile().toString();
				FileInputStream fileInputStream = new FileInputStream(xsltPath.toString());
				templateList.add(factory.newTemplates(new StreamSource(fileInputStream, systemId)));
				log.info((i + 1) + " Templates objects created, usedMemory = " + formatMemory(getUsedMemory()));
			}
			long usedMemoryAfter = getUsedMemory();
			log.info("usedMemoryAfter = "+formatMemory(usedMemoryAfter));
			long memorySpent = usedMemoryAfter - usedMemoryBefore;
			log.info("Memory spent for "+templateList.size()+" Templates objects: "+formatMemory(memorySpent)+"; avg "+formatMemory((memorySpent /templateList.size()))+" per object");
			log.info("=============================================");
			templateList.clear();
			templateList = null;
		}
	}

	@Ignore
	@Test
	public void testTransformerCreation(Path xslFilePath, int creationTimes) {
		boolean cacheEnabled = true;
		System.gc();
		long usedMemoryBefore = getUsedMemory();
		log.info("Run with cache = " + cacheEnabled + " on " + xslFilePath);
		log.info("Used Memory before: " + formatMemory(usedMemoryBefore));

		try {
			TransformerFactory transformerFactory = DelisTransformerFactory.newInstance(cacheEnabled);
			long start = System.currentTimeMillis();
			for (int i = 0; i < creationTimes; i++) {
				long startCase = System.currentTimeMillis();
				Transformer transformer = XSLTUtil.buildTransformer(new FileInputStream(xslFilePath.toFile()), xslFilePath.toAbsolutePath(), transformerFactory);
				log.info("Case " + i + " " + (System.currentTimeMillis() - startCase) + " ms, " + formatMemory(getUsedMemory())+", transformer: "+transformer);
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
