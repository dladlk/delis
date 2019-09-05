package dk.erst.delis.web.utilities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component("backs")
public class BacksUtility {

	@Autowired
	private WebRequest webRequest;
	
	public String get(String defaultView) {
		String backUrl = webRequest.getParameter("back");
		if (StringUtils.isEmpty(backUrl)) {
			return webRequest.getContextPath() + defaultView;
		}
		return backUrl;
	}

}
