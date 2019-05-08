package dk.erst.delis.data.util;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.enums.document.DocumentType;
import org.junit.Test;

import static org.junit.Assert.*;

public class BundleUtilTest {

    @Test
    public void checkBundlesForAllNamed() {
        check(DocumentType.class);

    }

    private void check(Class<? extends Enum> d) {
        for (Enum e : d.getEnumConstants()) {
            System.out.println("e = " + e);
        }
    }
}
