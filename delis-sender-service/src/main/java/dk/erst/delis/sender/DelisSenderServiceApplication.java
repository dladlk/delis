package dk.erst.delis.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("dk.erst.delis.data")
@ComponentScan("dk.erst.delis")
@EnableScheduling
@EnableJpaRepositories(basePackages="dk.erst.delis")
public class DelisSenderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelisSenderServiceApplication.class, args);
	}

}
