package dk.erst.delis.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

	@Configuration
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(WebSecurity web) throws Exception {
		    web.ignoring().antMatchers("/image/**");
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			UserBuilder users = User.withDefaultPasswordEncoder();
			auth.inMemoryAuthentication().getUserDetailsService().createUser(users.username("admin").password("password").authorities("ADMIN").build());
		}
	}
}
