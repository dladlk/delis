package dk.erst.delis.domibus.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BasicAuthEntryPoint authenticationEntryPoint;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, ConfigProperties configProperties) throws Exception {
		auth.inMemoryAuthentication().withUser(configProperties.getAuthUser()).password(passwordEncoder().encode(configProperties.getAuthPassword())).authorities("ROLE_USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated();
		http.csrf().disable();
		http.httpBasic().authenticationEntryPoint(authenticationEntryPoint);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
