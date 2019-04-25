package dk.erst.delis.web;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RedirectUtil {

	public static ResponseEntity<Object> redirectEntity(String servletContextPath, String url) {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    try {
			httpHeaders.setLocation(new URI(servletContextPath + url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
	}
}
