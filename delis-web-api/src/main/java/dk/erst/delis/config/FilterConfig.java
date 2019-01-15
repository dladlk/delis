package dk.erst.delis.config;

import dk.erst.delis.service.auth.AuthTokenFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author funtusthan, created by 12.01.19
 */

@Configuration
public class FilterConfig {

    private final AuthTokenFilter authTokenFilter;

    @Autowired
    public FilterConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> authFilterRegistration() {
        return getFilterRegistrationBean(authTokenFilter);
    }

    private FilterRegistrationBean<OncePerRequestFilter> getFilterRegistrationBean(OncePerRequestFilter filter){
        FilterRegistrationBean<OncePerRequestFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(filter);
        filterRegistration.addUrlPatterns("/rest/*");
        filterRegistration.setOrder(1);
        return filterRegistration;
    }
}
