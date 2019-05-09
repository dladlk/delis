package dk.erst.delis.data.util;

import dk.erst.delis.data.enums.Named;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@Slf4j
public class BundleUtilTest {

    private String DATA_ENUMS_PACKAGES = "dk.erst.delis.data.enums";
    private List<Enum<? extends Named>> missName = new ArrayList<>();

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

        List<String> keyPropertiesIsNotMiss = new ArrayList<>();

        if (!missName.isEmpty()) {
            for (Enum<? extends Named> v : missName) {
                StringBuilder sb = new StringBuilder();
                sb.append(v.getDeclaringClass().getSimpleName());
                sb.append('.');
                sb.append(v.name());
                sb.append("=");
                sb.append(StringUtils.capitaliseAllWords(v.name().replaceAll("_", " ").toLowerCase()));
                keyPropertiesIsNotMiss.add(sb.toString());
            }
        }

        if (!keyPropertiesIsNotMiss.isEmpty()) {
            log.error("This items are missing in messages.properties");
            keyPropertiesIsNotMiss.forEach(System.out::println);
        }

        assertTrue(keyPropertiesIsNotMiss.isEmpty());

    }

    private void check(Class<? extends Enum<? extends Named>> d) {
        for (Enum<? extends Named> e : d.getEnumConstants()) {
            Named n = (Named) e;
            String ename = n.getName();
            if (ename == null || e.name().equals(ename)) {
                /*
                 * CII is too short to give a translation for it :)
                 */
                if (ename.startsWith("CII") || ename.startsWith("BIS3") || ename.startsWith("OIOUBL")) {
                    continue;
                }
                missName.add(e);
            }
        }
    }
}
