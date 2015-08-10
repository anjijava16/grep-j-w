package com.gnts.erputil.tool;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import com.gnts.erputil.util.DateUtils;

public class EmailTrigger {
	// private String to ="soundar@gnts.in";
	// private String from = "soundar@gntstech.com";
	private String from = "webmaster@gntstech.com";
	// private String to,from;
	private Session session;
	private String To;
	private String hostname = "mail.gntstech.com";
	// private String hostname = "mail.gnts.in";
	private String portno = "25";
	private String password = "GntS@2013", messageBody = "", heading = "";
	private static Logger log = Logger.getLogger(EmailTrigger.class);
	
	public EmailTrigger(String to, String messagebody, String header) {
		messageBody = messagebody;
		To = to;
		heading = header;
		biuldview();
	}
	
	private void biuldview() {
		// TODO Auto-generated method stub
		log.info("MailTrigger called ");
		System.out.println("MailTrigger called---------------->");
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", hostname);
		log.info("MailTrigger Host name " + hostname);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", portno);
		try {
			log.info("Inside session try");
			session = Session.getInstance(props, new javax.mail.Authenticator() {
				public javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(from, password);
				}
			});
		}
		catch (Exception e) {
			System.out.println("Inside session catch" + e);
			log.info("Mail Trigger in session" + e);
		}
		log.info("MailTrigger After Session");
		// compose message
		try {
			log.info("MailTrigger in side try  ");
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));// change accordingly
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
			message.setSubject(heading);
			message.setText(messageBody);
			message.setSentDate(DateUtils.getcurrentdate());
			log.info("MailTrigger before send  ");
			// send message
			Transport.send(message);
			System.out.println("message sent as-------------->" + message);
			log.info("MailTrigger After send  ");
			System.out.println("message sent successfully");
		}
		catch (Exception e) {
			log.info("Exception fot Mail" + e);
			// throw new RuntimeException(e);
			e.printStackTrace();
		}
	}
}
