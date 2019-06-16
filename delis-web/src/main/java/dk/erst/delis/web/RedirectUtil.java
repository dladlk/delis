package dk.erst.delis.web;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RedirectUtil {

	public static ResponseEntity<Object> redirectEntity(String url) {
		ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext();
		String contextPath = servletContext.getContextPath();
		return redirectEntity(contextPath, url);
	}
	
	private static ResponseEntity<Object> redirectEntity(String servletContextPath, String url) {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    try {
			httpHeaders.setLocation(new URI(servletContextPath + url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
	}
}
