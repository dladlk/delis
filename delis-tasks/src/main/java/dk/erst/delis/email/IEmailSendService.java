package dk.erst.delis.email;

import dk.erst.delis.task.email.EmailData;

public interface IEmailSendService {

	boolean send(EmailData emailData);

}