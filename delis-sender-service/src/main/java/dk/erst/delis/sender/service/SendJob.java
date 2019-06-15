package dk.erst.delis.sender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SendJob {

	@Autowired
	private SendService sendService;

	@Scheduled(fixedDelay = 10000, initialDelay = 0)
	public void execute() {
		try {
			sendService.process();
		} catch (Throwable t) {
			log.error("Failed sendService invocation", t);
		}
	}

}
