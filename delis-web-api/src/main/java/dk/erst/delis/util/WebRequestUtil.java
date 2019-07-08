package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;

import lombok.experimental.UtilityClass;

import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;

@UtilityClass
public class WebRequestUtil {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final int PAGE_PARAM_VALUE = 1;
    private static final int SIZE_PARAM_VALUE = 10;
    private static final String FLAG_PARAM_START_WITH = "statusError";

    public PageAndSizeModel generatePageAndSizeModel(WebRequest webRequest) {
        int page = webRequest.getParameter(PAGE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(PAGE_PARAM))) : PAGE_PARAM_VALUE;
        int size = webRequest.getParameter(SIZE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(SIZE_PARAM))) : SIZE_PARAM_VALUE;
        return new PageAndSizeModel(page, size);
    }

    DateRangeModel generateDateRange(String timePattern) {
        String[] times = timePattern.split(":");
        long startDate = Long.parseLong(times[0]);
        long endDate = Long.parseLong(times[1]);
        if (startDate == endDate) {
            return new DateRangeModel(DateUtil.generateBeginningOfDay(new Date(startDate)), new Date(endDate));
        } else {
            return new DateRangeModel(new Date(startDate), new Date(endDate));
        }
    }

    public boolean existErrorFlagParameter(WebRequest webRequest) {
        String statusError = webRequest
                .getParameterMap()
                .keySet()
                .stream()
                .filter(key -> key.startsWith(FLAG_PARAM_START_WITH))
                .findFirst().orElse(null);
        return Boolean.parseBoolean(statusError);
    }
}
