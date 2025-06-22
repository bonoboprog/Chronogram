package it.unicas.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class EmailService {

    private static final Logger logger = LogManager.getLogger(EmailService.class);
    private final Properties mailProperties;
    private final String mailUser;
    private final String mailPassword;

    public EmailService() {
        // Legge le credenziali e le configurazioni direttamente dalle variabili d'ambiente
        this.mailUser = System.getenv("MAIL_USER");
        this.mailPassword = System.getenv("MAIL_PASSWORD");
        
        // Se le variabili non sono impostate, logga un errore grave
        if (this.mailUser == null || this.mailPassword == null) {
            logger.fatal("SMTP environment variables MAIL_USER and/or MAIL_PASSWORD are not set!");
            // Lancia un'eccezione per bloccare l'avvio o l'uso del servizio
            throw new IllegalStateException("SMTP credentials are not configured.");
        }

        this.mailProperties = createMailProperties();
    }

    private Properties createMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", System.getenv("MAIL_HOST"));
        props.put("mail.smtp.port", System.getenv("MAIL_PORT"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    public void sendPasswordResetEmail(String toEmail, String token) throws ServiceException {
        logger.info("Preparing to send password reset email to {}", toEmail);

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Usa le credenziali lette dalle variabili d'ambiente
                return new PasswordAuthentication(mailUser, mailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.mailUser));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Request");

            String resetUrl = "http://localhost:3000/reset-password?token=" + token; // L'URL del tuo frontend
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
        // ... (contenuto HTML dell'email, rimane invariato) ...
        return "<html><body>...<a href=\"" + resetUrl + "\">Reset...</a>...</body></html>";
    }
}
