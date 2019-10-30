package dk.erst.delis.web.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dk.erst.delis.task.email.EmailData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailSendService implements IEmailSendService {

	private JavaMailSender emailSender;

	@Autowired
	public EmailSendService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	@Override
	public boolean send(EmailData emailData) {
		long start = System.currentTimeMillis();
		try {
			log.info("Start email sending to " + emailData.getTo() + ", subject " + emailData.getSubject());
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(emailData.getFrom());
			message.setTo(emailData.getTo().split(";"));
			message.setSubject(emailData.getSubject());
			message.setText(emailData.getBody());
			emailSender.send(message);
			return true;
		} catch (Exception e) {
			log.error("Failed to send email " + emailData, e);
			return false;
		} finally {
			log.info("Done email sending in " + (System.currentTimeMillis() - start) + " ms to " + emailData.getTo() + ", subject " + emailData.getSubject());
		}
	}

}
