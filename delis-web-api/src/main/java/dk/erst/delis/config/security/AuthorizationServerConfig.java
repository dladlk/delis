package dk.erst.delis.config.security;

import dk.erst.delis.exception.oauth.CustomOauthException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author funtusthan, created by 22.03.19
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.client.id}")
    private String id;

    @Value("${security.client.secret}")
    private String secret;

    @Value("${security.client.pwd}")
    private String pwd;

    @Value("${security.client.code}")
    private String code;

    @Value("${security.client.refresh.token}")
    private String refreshToken;

    @Value("${security.client.refresh.token.valid.time}")
    private int accessTokenValidTime;

    @Value("${security.client.access.token.valid.time}")
    private int refreshTokenValidTime;

    @Value("${security.client.implicit}")
    private String implicit;

    @Value("${security.client.role}")
    private String role;

    @Value("${security.client.trusted}")
    private String trusted;

    @Value("${security.client.read}")
    private String read;

    @Value("${security.client.write}")
    private String write;

    @Value("${security.client.trust}")
    private String trust;

    private final UserApprovalHandler userApprovalHandler;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorizationServerConfig(UserApprovalHandler userApprovalHandler, AuthenticationManager authenticationManager, TokenStore tokenStore, PasswordEncoder passwordEncoder) {
        this.userApprovalHandler = userApprovalHandler;
        this.authenticationManager = authenticationManager;
        this.tokenStore = tokenStore;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(id)
                .authorizedGrantTypes(pwd, code, refreshToken, implicit)
                .authorities(role, trusted)
                .scopes(read, write, trust)
                .secret(passwordEncoder.encode(secret))
                .accessTokenValiditySeconds(accessTokenValidTime)//Access token is only valid for 24 hrs.
                .refreshTokenValiditySeconds(refreshTokenValidTime);//Refresh token is only valid for 30 days.
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(new CustomAccessTokenConverter())
                .tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager)
                .exceptionTranslator(exception -> {
                    System.out.println("exception == " + exception.getClass().getName());
                    if (exception instanceof OAuth2Exception) {
                        OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(new CustomOauthException(oAuth2Exception.getMessage()));
                    } else {
                        throw exception;
                    }
                });
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("hasRole('ROLE_DELIS_SERVER')")
                .checkTokenAccess("hasRole('ROLE_DELIS_SERVER')");

    }
}
