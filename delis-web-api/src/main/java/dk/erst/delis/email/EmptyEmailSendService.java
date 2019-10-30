package dk.erst.delis.email;

import org.springframework.stereotype.Service;

import dk.erst.delis.task.email.EmailData;
import dk.erst.delis.web.email.IEmailSendService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmptyEmailSendService implements IEmailSendService {

	@Override
	public boolean send(EmailData emailData) {
		log.warn("Email sending from web-api is not implemented");
		return false;
	}

}
