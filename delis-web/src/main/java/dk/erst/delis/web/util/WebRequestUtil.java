package dk.erst.delis.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.erst.delis.web.container.PageDataContainer;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@UtilityClass
public class WebRequestUtil {

    public int pageParam(WebRequest webRequest) {
        int page = 1;
        String pageParam = webRequest.getParameter("page");
        if (StringUtils.isNotBlank(pageParam)) {
            page = Integer.parseInt(pageParam);
            if (page == 0) {
                page = 1;
            }
        }
        return page;
    }

    public int sizeParam(WebRequest webRequest) {
        int size = 10;
        String sizeParam = webRequest.getParameter("size");
        if (StringUtils.isNotBlank(sizeParam)) {
            size = Integer.parseInt(sizeParam);
            if (size == 0) {
                size = 10;
            }
        }
        return size;
    }
    
    public String[] orderParam(WebRequest webRequest) {
    	String[] orderBy = new String[2];
        String pageParam = webRequest.getParameter("orderBy");
        if (StringUtils.isNotBlank(pageParam)) {
        	orderBy = pageParam.split("_");
            return orderBy;
        }
        return new String[] {"1", "desc"};
    }

    public PageDataContainer getPageDataContainerByWebRequest(WebRequest webRequest) {
        String datatableParam = webRequest.getParameter("datatable");
        if (StringUtils.isBlank(datatableParam)) {
            return null;
        }
        PageDataContainer pageDataContainer = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            pageDataContainer = mapper.readValue(datatableParam, PageDataContainer.class);
            return pageDataContainer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
