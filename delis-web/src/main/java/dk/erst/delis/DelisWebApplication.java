package dk.erst.delis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import dk.erst.delis.config.ConfigProperties;

@SpringBootApplication
@EntityScan("dk.erst.delis.data")
@EnableConfigurationProperties(ConfigProperties.class)
@EnableJpaAuditing
public class DelisWebApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DelisWebApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(DelisWebApplication.class, args);
	}
}
