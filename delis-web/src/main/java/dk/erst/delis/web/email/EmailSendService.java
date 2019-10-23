package dk.erst.delis.web.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dk.erst.delis.task.email.EmailData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailSendService {

	private JavaMailSender emailSender;

	@Autowired
	public EmailSendService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	public boolean send(EmailData emailData) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(emailData.getFrom());
			message.setTo(emailData.getTo());
			message.setSubject(emailData.getSubject());
			message.setText(emailData.getBody());
			emailSender.send(message);
			return true;
		} catch (Exception e) {
			log.error("Failed to send email " + emailData, e);
			return false;
		}
	}

}
