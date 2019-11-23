package dk.erst.delis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@EnableScheduling
@SpringBootApplication
@EntityScan("dk.erst.delis.data")
/*
 * Critical - when annotation @ComponentScan was defined to include another package, 
 * @DataJpaTest annotation stopped filtering controllers and tests required servletContext, which was not initialized
 */
//@ComponentScan(value = { "dk.erst.delis", "org.springframework.data.jpa.datatables.easy" })
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableJpaAuditing
public class DelisWebApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DelisWebApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(DelisWebApplication.class, args);
	}
	
	@Bean(name = "messageSource")
	public MessageSource errorMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/error_messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
	    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
	    bean.setValidationMessageSource(errorMessageSource());
	    return bean;
	}
}
