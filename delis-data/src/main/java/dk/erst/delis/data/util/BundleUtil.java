package dk.erst.delis.data.util;

import dk.erst.delis.data.enums.Named;
import lombok.experimental.UtilityClass;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@UtilityClass
public class BundleUtil {

    private final ResourceBundle RESOURCE = ResourceBundle.getBundle("messages");

    public String getName(Enum<? extends Named> e) {
        String m = BundleUtil.getMessage(e.getClass().getSimpleName()+"." + e.name());
        if (m == null) {
            return e.name();
        }
        return m;
    }

    private String getMessage(String name) {
        try {
            return RESOURCE.getString(name);
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
