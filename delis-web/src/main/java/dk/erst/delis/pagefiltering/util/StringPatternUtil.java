package dk.erst.delis.pagefiltering.util;

import lombok.experimental.UtilityClass;

/**
 * @author funtusthan, created by 13.01.19
 */

@UtilityClass
class StringPatternUtil {

    private static final String REGEX_CREATE_TIME_RANGE = "^[0-9]{13}:[0-9]{13}$";

    String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }

    boolean dateRegExPattern(String parameter) {
        return parameter.matches(REGEX_CREATE_TIME_RANGE);
    }
}
