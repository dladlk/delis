package dk.erst.delis.config.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
		public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

			@Override
			public void onApplicationEvent(AuthenticationSuccessEvent evt) {
				String login = evt.getAuthentication().getName();
				CustomUserDetails user = (CustomUserDetails) evt.getAuthentication().getPrincipal();
				if (!user.getAuthorities().contains(GlobalController.ADMIN_AUTHORITY)) {
					throw new InsufficientAuthenticationException("User " + login + " has no access to DELIS Setup Cockpit, please login to main DELIS web interface");
				}
				if (user.isDisabled()) {
					throw new DisabledException("User " + login + " has no access to DELIS Setup Cockpit, please login to main DELIS web interface");
				}
			}
		}
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        	ExceptionMappingAuthenticationFailureHandler authFailureHandler = new ExceptionMappingAuthenticationFailureHandler();
        	authFailureHandler.setDefaultFailureUrl("/login?error=true");
        	Map<String, String> failureUrlMap = new HashMap<String, String>();
        	failureUrlMap.put("org.springframework.security.authentication.InsufficientAuthenticationException", "/login?error=user");
        	failureUrlMap.put("org.springframework.security.authentication.DisabledException", "/login?error=disabled");
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
