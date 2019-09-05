package dk.erst.delis.web.datatables.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import dk.erst.delis.web.datatables.web.PageDataBuilder;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData implements Serializable {

	private static final long serialVersionUID = 6895372900532257655L;

	private Map<Class<?>, PageData> pageStateByControllerClass = new HashMap<>();

	public PageData getOrCreatePageData(Class<?> controllerClass, PageDataBuilder builder) {
		PageData pageData = pageStateByControllerClass.get(controllerClass);
		if (pageData == null) {
			pageData = builder.buildInitialPageData();
			pageStateByControllerClass.put(controllerClass, pageData);
		}
		return pageData;
	}

}
