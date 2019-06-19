package dk.erst.delis.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BundleUtilTest {

	private String DATA_ENUMS_PACKAGES = "dk.erst.delis.data.enums";
	private List<Enum<? extends Named>> missName = new ArrayList<>();
	private List<String> found = new ArrayList<>();

	@Test
	public void checkDanishName() {
		assertEquals("Ukendt", DocumentFormatFamily.UNSUPPORTED.getNameDa());
		assertEquals("Unsupported", DocumentFormatFamily.UNSUPPORTED.getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void checkBundlesForAllNamed() throws ClassNotFoundException {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(Named.class));
		Set<BeanDefinition> res = scanner.findCandidateComponents(DATA_ENUMS_PACKAGES);
		for (BeanDefinition b : res) {
			Class<?> c = Class.forName(b.getBeanClassName());
			check((Class<? extends Enum<? extends Named>>) c);
		}

		List<String> missed = new ArrayList<>();

		if (!missName.isEmpty()) {
			for (Enum<? extends Named> v : missName) {
				StringBuilder sb = new StringBuilder();
				sb.append(v.getDeclaringClass().getSimpleName());
				sb.append('.');
				sb.append(v.name());
				sb.append("=");
				sb.append(StringUtils.capitaliseAllWords(v.name().replaceAll("_", " ").toLowerCase()));
				missed.add(sb.toString());
			}
		}

		if (!missed.isEmpty()) {
			log.error("This items are missing in messages.properties:\n");
			missed.forEach(log::error);
		}

		assertTrue(missed.isEmpty());
		assertTrue("Expected found bundles size is more than actual " + found.size(), found.size() > 30);

	}

	private void check(Class<? extends Enum<? extends Named>> d) {
		for (Enum<? extends Named> e : d.getEnumConstants()) {
			Named n = (Named) e;
			checkResult(e, n.getName());
			checkResult(e, n.getNameDa());
		}
	}

	private void checkResult(Enum<? extends Named> e, String ename) {
		if (ename == null || e.name().equals(ename)) {
			/*
			 * Some names are too short to give a translation for it.
			 */
			if (ename.equals("CII") || ename.equals("BIS3") || ename.equals("OIOUBL")) {
				return;
			}
			missName.add(e);
		} else {
			found.add(ename);
		}
	}
}
