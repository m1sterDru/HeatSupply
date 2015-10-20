package nik.heatsupply.mail;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSender {
	private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
 
	public void generateAndSendEmail(String recipient, 
			String title, String emailBody, String filename) throws AddressException, MessagingException {

		mailServerProperties = getPropValues("mail.properties");
		String user = mailServerProperties.getProperty("mail.address");
		user = user.indexOf("@") > 0 ? user : user + "@gmail.com";
		String password = mailServerProperties.getProperty("mail.password");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);

		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setHeader("Content-Type", "text/plain; charset=UTF-8");
		generateMailMessage.addRecipient(Message.RecipientType.TO, 
				new InternetAddress(recipient));
		generateMailMessage.setSubject(title, "utf-8");
		
		// Create the message part 
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setHeader("Content-Type", "text/plain; charset=UTF-8");
		messageBodyPart.setContent(emailBody, "text/html; charset=utf-8");
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (filename != null) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename.substring(filename.lastIndexOf("/") + 1));
			multipart.addBodyPart(messageBodyPart);
		}
		generateMailMessage.setContent(multipart);
		
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", user, password);
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
	
	private Properties getPropValues(String propFileName) {
		Properties prop = null;

		try(InputStream  inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);) {
			if (inputStream != null) {
				prop = new Properties();
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getFullStackTrace(e));
		}
		return prop;
	}
}