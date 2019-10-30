package dk.erst.delis.web.email;

import dk.erst.delis.task.email.EmailData;

public interface IEmailSendService {

	boolean send(EmailData emailData);

}