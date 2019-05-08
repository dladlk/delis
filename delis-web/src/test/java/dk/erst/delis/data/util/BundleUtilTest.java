package dk.erst.delis.data.util;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.enums.document.DocumentType;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BundleUtilTest {

    private List<Enum> missName = new ArrayList<>();

    @Test
    public void checkBundlesForAllNamed() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Named.class));
        Set<BeanDefinition> res = scanner.findCandidateComponents(DocumentType.class.getPackage().getName());
        for (BeanDefinition b : res) {
            Class<?> c = Class.forName(b.getBeanClassName());
            check((Class<? extends Enum>) c);
        }

        if (!missName.isEmpty()) {
            for (Enum v : missName) {
                StringBuilder sb = new StringBuilder();
                sb.append(v.getDeclaringClass().getSimpleName());
                sb.append('.');
                sb.append(v.name());
                sb.append("=");
                sb.append(StringUtils.capitalizeFirstLetter(v.name().replaceAll("_", "").toLowerCase()));
                System.out.println(sb.toString());
            }
        }
        assertEquals("Some Named enums miss value in messages.properties", 0, missName.size());
    }

    private void check(Class<? extends Enum> d) {
        for (Enum e : d.getEnumConstants()) {
            Named n = (Named) e;
            String ename = n.getName();
            if (ename == null || e.name().equals(ename)) {
                missName.add(e);
            }
        }
    }
}
