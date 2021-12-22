package dk.erst.delis.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import dk.erst.delis.persistence.repository.tokens.OAuthAccessTokenRepository;
import dk.erst.delis.persistence.repository.tokens.OAuthRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Order
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientDetailsService clientDetailsService;
    private final CustomUserDetailsService userDetailsService;
    private final OAuthAccessTokenRepository oAuthAccessTokenRepository;
    private final OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    @Autowired
    public OAuth2SecurityConfig(
            ClientDetailsService clientDetailsService,
            CustomUserDetailsService userDetailsService,
            OAuthAccessTokenRepository oAuthAccessTokenRepository,
            OAuthRefreshTokenRepository oAuthRefreshTokenRepository) {
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.oAuthRefreshTokenRepository = oAuthRefreshTokenRepository;
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout().logoutUrl("/rest/logout").clearAuthentication(true).permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/token").permitAll();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomTokenStore(oAuthAccessTokenRepository, oAuthRefreshTokenRepository);
    }

    @Bean
    @Autowired
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);
        return handler;
    }

    @Bean
    @Autowired
    public ApprovalStore approvalStore(TokenStore tokenStore) {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Component
	@Slf4j
	public static class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

		@Autowired
		private CustomUserDetailsService customUserDetailsService;

		@Override
		public void onApplicationEvent(AuthenticationSuccessEvent evt) {
			String login = evt.getAuthentication().getName();
			log.info("User " + login + " successfully logged in");
			customUserDetailsService.successfulLogin(login);
		}
	}

	@Component
	@Slf4j
	public static class LoginListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

		@Autowired
		private CustomUserDetailsService customUserDetailsService;

		@Override
		public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent evt) {
			String userLogin = (String) evt.getAuthentication().getPrincipal();
			log.info("Bad credentials for user " + userLogin);
			customUserDetailsService.badCredentials(userLogin);
		}
	}
}
