package dk.erst.delis.exception.oauth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author funtusthan, created by 28.03.19
 */

@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {

    public CustomOauthException(String msg) {
        super(msg);
    }
}
