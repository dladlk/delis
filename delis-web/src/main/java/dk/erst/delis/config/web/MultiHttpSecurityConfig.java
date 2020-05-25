package dk.erst.delis.config.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import dk.erst.delis.config.web.security.CustomUserDetails;
import dk.erst.delis.config.web.security.CustomUserDetailsService;
import dk.erst.delis.web.main.GlobalController;
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

        @Configuration
        @Order (1)
        public static class RestSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

            @Autowired
            public RestSecurityConfigurerAdapter() {
            }

            @Override
            protected void configure(HttpSecurity http) throws Exception {

                http.antMatcher("/rest/**").authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .httpBasic();
            }
        }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final CustomUserDetailsService customUserDetailsService;

        @Autowired
        public FormLoginWebSecurityConfigurerAdapter(CustomUserDetailsService customUserDetailsService) {
            this.customUserDetailsService = customUserDetailsService;
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers("/actuator/**", "/actuator")
                    .antMatchers("/image/**")
                    .antMatchers("/css/**")
                    .antMatchers("/js/**")
                    .antMatchers("/v2/api-docs")
                    .antMatchers("/swagger-resources/**")
                    .antMatchers("/configuration/**")
                    .antMatchers("/swagger*/**")
                    .antMatchers("/rest/open/**")
                    .antMatchers("/webjars/**");
        }
        
		@Component
		@Slf4j
		public static class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
			
			@Autowired
			private CustomUserDetailsService customUserDetailsService;
			
			@Override
			public void onApplicationEvent(AuthenticationSuccessEvent evt) {
				String login = evt.getAuthentication().getName();
				CustomUserDetails user = (CustomUserDetails) evt.getAuthentication().getPrincipal();

				if (!user.getAuthorities().contains(GlobalController.ADMIN_AUTHORITY)) {
					String message = "User " + login + " has no access to DELIS Setup Cockpit, please login to main DELIS web interface";
					log.warn(message);
					throw new InsufficientAuthenticationException(message);
				}
				
				log.info("User "+login+" successfully logged in");
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
				log.info("Bad credentials for user "+userLogin);
				
				customUserDetailsService.badCredentials(userLogin);
			}
		}
		
		public static class CustomAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {
			
			private String defaultFailureUrl;
			private Map<String, String> failureUrlMap;

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
				String url = failureUrlMap.get(exception.getClass().getName());
				
				String currentPrincipalName= request.getParameter("username");
				if (url != null) {
					getRedirectStrategy().sendRedirect(request, response, url + "&username="+currentPrincipalName);
				} else {
					saveException(request, exception);
					getRedirectStrategy().sendRedirect(request, response, defaultFailureUrl + "&username="+currentPrincipalName);
				}
			}

			@Override
			public void setDefaultFailureUrl(String defaultFailureUrl) {
				super.setDefaultFailureUrl(defaultFailureUrl);
				this.defaultFailureUrl = defaultFailureUrl;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void setExceptionMappings(Map<?, ?> failureUrlMap) {
				super.setExceptionMappings(failureUrlMap);
				this.failureUrlMap = (Map<String,String>)failureUrlMap;
			}
		}
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        	CustomAuthenticationFailureHandler authFailureHandler = new CustomAuthenticationFailureHandler();
        	Map<String, String> failureUrlMap = new HashMap<String, String>();
        	failureUrlMap.put("org.springframework.security.authentication.InsufficientAuthenticationException", "/login?error=user");
        	failureUrlMap.put("org.springframework.security.authentication.DisabledException", "/login?error=disabled");
        	failureUrlMap.put("org.springframework.security.authentication.LockedException", "/login?error=locked");
        	failureUrlMap.put("org.springframework.security.authentication.CredentialsExpiredException", "/login?error=passexpired");
        	authFailureHandler.setDefaultFailureUrl("/login?error=true");
			authFailureHandler.setExceptionMappings(failureUrlMap);
        	
            http.csrf().disable();
            http.authorizeRequests().antMatchers("/login", "/logout", "/default/user", "/swagger*", "/configuration/**", "/swagger-resources/**", "/v2/api-docs").permitAll();
            http.authorizeRequests().anyRequest().authenticated().and().formLogin()
                    .loginProcessingUrl("/j_spring_security_check")
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/home")
                    .failureHandler(authFailureHandler)
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");

            http.authorizeRequests().and()
                    .rememberMe().tokenRepository(this.persistentTokenRepository()) //
                    .tokenValiditySeconds(60);
        }

        @Bean
        public PersistentTokenRepository persistentTokenRepository() {
            return new InMemoryTokenRepositoryImpl();
        }

        @Bean
        public HttpFirewall allowUrlEncodedPercentHttpFirewall() {
            StrictHttpFirewall firewall = new StrictHttpFirewall();
            firewall.setAllowUrlEncodedPercent(true);
            return firewall;
        }
    }
}
