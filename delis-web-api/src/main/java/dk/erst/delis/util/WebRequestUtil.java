package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;
import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.rest.data.request.param.SortModel;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;

@UtilityClass
public class WebRequestUtil {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_PARAM = "sort";
    private static final String ORDER_PARAM = "order";
    private static final int DEFAULT_PAGE_PARAM_VALUE = 0;
    private static final int DEFAULT_SIZE_PARAM_VALUE = 10;
    private static final String DEFAULT_SORT_PARAM_VALUE = "createTime";
    private static final String DEFAULT_ORDER_PARAM_VALUE = "desc";
    private static final String FLAG_PARAM_START_WITH = "flagParam";

    public PageAndSizeModel generatePageAndSizeModel(WebRequest webRequest) {
        int page = webRequest.getParameter(PAGE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(PAGE_PARAM))) : DEFAULT_PAGE_PARAM_VALUE;
        int size = webRequest.getParameter(SIZE_PARAM) != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter(SIZE_PARAM))) : DEFAULT_SIZE_PARAM_VALUE;
        return new PageAndSizeModel(page, size);
    }

    public SortModel generateSortModel(WebRequest webRequest) {
        String sort = StringUtils.isNotBlank(webRequest.getParameter(SORT_PARAM)) ? Objects.requireNonNull(webRequest.getParameter(SORT_PARAM)) : DEFAULT_SORT_PARAM_VALUE;
        String order = StringUtils.isNotBlank(webRequest.getParameter(ORDER_PARAM)) ? Objects.requireNonNull(webRequest.getParameter(ORDER_PARAM)) : DEFAULT_ORDER_PARAM_VALUE;
        return new SortModel(sort, order);
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

    public String existFlagParameter(WebRequest webRequest) {
        return webRequest
                .getParameterMap()
                .keySet()
                .stream()
                .filter(key -> key.startsWith(FLAG_PARAM_START_WITH))
                .findFirst().orElse(null);
    }
}
