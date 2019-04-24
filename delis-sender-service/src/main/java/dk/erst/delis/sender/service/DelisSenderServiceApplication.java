package dk.erst.delis.sender.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("dk.erst.delis.data")
@EnableScheduling
public class DelisSenderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelisSenderServiceApplication.class, args);
	}

}
