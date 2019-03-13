package dk.erst.delis.config.web;

import dk.erst.delis.config.web.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

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
                    .antMatchers("/image/**")
                    .antMatchers("/css/**")
                    .antMatchers("/js/**")
                    .antMatchers("/v2/api-docs")
                    .antMatchers("/swagger-resources/**")
                    .antMatchers("/configuration/**")
                    .antMatchers("/swagger*/**")
                    .antMatchers("/webjars/**")
                    .antMatchers("/rest/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable();
            http.authorizeRequests().antMatchers("/login", "/logout", "/default/user", "/swagger*", "/configuration/**", "/swagger-resources/**", "/v2/api-docs").permitAll();
            http.authorizeRequests().anyRequest().authenticated();
            http.authorizeRequests().and().formLogin()
                    .loginProcessingUrl("/j_spring_security_check")
                    .loginPage("/login")
                    .defaultSuccessUrl("/home")
                    .failureUrl("/login?error=true")
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
    }
}
