package dk.erst.delis.handler;

import dk.erst.delis.config.security.CustomUserDetails;
import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.rest.data.response.auth.AuthData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@SuppressWarnings("ALL")
@ControllerAdvice
public class OauthResponseHandler implements ResponseBodyAdvice<Object> {

    private final TokenStore tokenStore;

    @Autowired
    public OauthResponseHandler(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter methodParameter,
            MediaType mediaType,
            Class<? extends HttpMessageConverter<?>> aClass,
            ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse) {

        if (body instanceof DefaultOAuth2AccessToken) {
            DefaultOAuth2AccessToken defaultOAuth2AccessToken = (DefaultOAuth2AccessToken) body;
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(tokenStore.readAccessToken(defaultOAuth2AccessToken.getValue()));
            CustomUserDetails userDetails = (CustomUserDetails) oAuth2Authentication.getUserAuthentication().getPrincipal();
            AuthData authData = new AuthData();
            authData.setRole(userDetails.getRole());
            authData.setUsername(userDetails.getUserName());
            authData.setAccessToken(defaultOAuth2AccessToken.getValue());
            authData.setRefreshToken(defaultOAuth2AccessToken.getRefreshToken().getValue());
            return new DataContainer<>(authData);
        } else {
            return body;
        }
    }
}
