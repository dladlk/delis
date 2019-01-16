package dk.erst.delis.util;

import lombok.experimental.UtilityClass;

/**
 * @author funtusthan, created by 13.01.19
 */

@UtilityClass
public class StringPatternUtil {

    public String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
