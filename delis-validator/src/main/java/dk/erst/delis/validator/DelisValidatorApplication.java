package dk.erst.delis.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "dk.erst.delis.validator", "dk.erst.delis.web.utilities" })
public class DelisValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelisValidatorApplication.class, args);
	}

}
