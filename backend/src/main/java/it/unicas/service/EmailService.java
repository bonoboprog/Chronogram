package it.unicas.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static final Logger logger = LogManager.getLogger(EmailService.class);
    private final Properties mailProperties;

    public EmailService() {
        this.mailProperties = loadMailProperties();
    }

    public void sendPasswordResetEmail(String toEmail, String token) throws ServiceException {
        logger.info("Preparing to send password reset email to {}", toEmail);

        // Prepara la sessione con il server SMTP
        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getProperty("mail.smtp.user"),
                                                mailProperties.getProperty("mail.smtp.password"));
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Request");

            String resetUrl = "http://localhost:3000/reset-password?token=" + token;
            String emailContent = createHtmlEmailBody(resetUrl);

            message.setContent(emailContent, "text/html; charset=utf-8");

            Transport.send(message);

            logger.info("Password reset email successfully sent to {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", toEmail, e);
            throw new ServiceException("Could not send the password reset email.");
        }
    }

    private String createHtmlEmailBody(String resetUrl) {
        return "<html>" +
               "<body>" +
               "<h2>Password Reset Request</h2>" +
               "<p>We received a request to reset your password. Click the link below to set a new password:</p>" +
               "<p><a href=\"" + resetUrl + "\">Reset Your Password</a></p>" +
               "<p>This link will expire in 30 minutes.</p>" +
               "<p>If you did not request a password reset, please ignore this email.</p>" +
               "</body>" +
               "</html>";
    }

    private Properties loadMailProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                logger.error("Sorry, unable to find mail.properties");
                return props;
            }
            props.load(input);
        } catch (Exception e) {
            logger.error("Error loading mail.properties", e);
        }
        return props;
    }
}
