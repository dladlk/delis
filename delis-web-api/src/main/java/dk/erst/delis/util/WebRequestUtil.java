package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.ObjectUtils;

import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;

/**
 * @author funtusthan, created by 14.01.19
 */

@UtilityClass
public class WebRequestUtil {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final int PAGE_PARAM_VALUE = 1;
    private static final int SIZE_PARAM_VALUE = 10;
    private static final String SORT_PARAM_START_WITH = "countClick";
    private static final String SORT_PARAM_DEFAULT_VALUE = "0";
    private static final String RANGE_DATE_START_PARAM = "start";
    private static final String RANGE_DATE_END_PARAM = "end";

    public PageAndSizeModel generatePageAndSizeModel(WebRequest webRequest) {
        int page = webRequest.getParameter(PAGE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(PAGE_PARAM))) : PAGE_PARAM_VALUE;
        int size = webRequest.getParameter(SIZE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(SIZE_PARAM))) : SIZE_PARAM_VALUE;
        return new PageAndSizeModel(page, size);
    }

    public DateRangeModel generateDateRequestModel(WebRequest webRequest) {
        if (Objects.nonNull(webRequest.getParameter(RANGE_DATE_START_PARAM)) && Objects.nonNull(webRequest.getParameter(RANGE_DATE_END_PARAM))) {
            long startDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter(RANGE_DATE_START_PARAM)));
            long endDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter(RANGE_DATE_END_PARAM)));
            return new DateRangeModel(new Date(startDate), new Date(endDate));
        }
        return null;
    }

    public DateRangeModel generateDateRange(String timePattern) {
        String[] times = timePattern.split(":");
        long startDate = Long.parseLong(times[0]);
        long endDate = Long.parseLong(times[1]);
        return new DateRangeModel(new Date(startDate), new Date(endDate));
    }

    public String existSortParameter(WebRequest webRequest) {
        return webRequest
                .getParameterMap()
                .keySet()
                .stream()
                .filter(key -> key.startsWith(SORT_PARAM_START_WITH) && ObjectUtils.notEqual(webRequest.getParameter(key), SORT_PARAM_DEFAULT_VALUE))
                .findFirst().orElse(null);
    }
}
