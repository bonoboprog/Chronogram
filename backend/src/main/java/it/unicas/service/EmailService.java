package it.unicas.service;

import it.unicas.service.exception.ServiceException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Service dedicato all'invio di email transazionali.
 * Legge la configurazione SMTP dalle variabili d'ambiente del sistema.
 */
public class EmailService {

    private static final Logger logger = LogManager.getLogger(EmailService.class);
    
    private final Properties mailProperties;
    private final String mailUser;
    private final String mailPassword;

    /**
     * Costruisce l'EmailService.
     * Legge la configurazione SMTP richiesta dalle variabili d'ambiente.
     * Lancia una IllegalStateException se le credenziali non sono configurate,
     * per bloccare l'avvio dell'applicazione in uno stato non funzionante.
     */
    public EmailService() {
        // Legge le credenziali e le configurazioni direttamente dalle variabili d'ambiente
        this.mailUser = System.getenv("MAIL_USER");
        this.mailPassword = System.getenv("MAIL_PASSWORD");
        
        // Controlli di robustezza: se le variabili non sono impostate, l'applicazione non può funzionare correttamente.
        if (this.mailUser == null || this.mailUser.trim().isEmpty() ||
            this.mailPassword == null || this.mailPassword.trim().isEmpty()) {
            
            logger.fatal("FATAL: Environment variables MAIL_USER and/or MAIL_PASSWORD are not set!");
            throw new IllegalStateException("SMTP credentials are not configured in the environment.");
        }

        this.mailProperties = createMailProperties();
    }

    /**
     * Invia un'email per il reset della password.
     * @param toEmail L'indirizzo email del destinatario.
     * @param token Il token completo (selector:verifier) da includere nel link di reset.
     * @throws ServiceException se si verifica un errore durante l'invio dell'email.
     */
    public void sendPasswordResetEmail(String toEmail, String token, String origin) throws ServiceException {
        logger.info("Preparing to send password reset email to {} from origin {}", toEmail, origin);

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
            message.setSubject("Richiesta di Reset Password - Chronogram");

           
            // === INIZIO MODIFICA ===
            String baseUrl;
            // Se l'header Origin è presente e valido, usalo.
            if (origin != null && !origin.trim().isEmpty()) {
                baseUrl = origin;
            } else {
                // Altrimenti, usa il valore di fallback dalle variabili d'ambiente.
                baseUrl =  "http://localhost:8100";
                logger.warn("Origin header was not present. Falling back to APP_BASE_URL: {}", baseUrl);
            }

            // Costruisci l'URL dinamicamente
            String resetUrl = baseUrl + "/reset-password?token=" + token;
            // === FINE MODIFICA ===

            String emailContent = createHtmlEmailBody(resetUrl);
            message.setContent(emailContent, "text/html; charset=utf-8");
            Transport.send(message);

            logger.info("Password reset email successfully sent to {}", toEmail);

            
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", toEmail, e);
            throw new ServiceException("Could not send the password reset email.");
        }
    }

    /**
     * Crea il corpo dell'email in formato HTML.
     * @param resetUrl L'URL completo per il reset della password.
     * @return una stringa contenente il codice HTML dell'email.
     */
    private String createHtmlEmailBody(String resetUrl) {
    return "<!DOCTYPE html>" +
           "<html lang=\"en\">" +
           "<head>" +
           "<meta charset=\"UTF-8\">" +
           "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
           "<style>" +
           "  body { font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
           "  .container { width: 90%; max-width: 600px; margin: 40px auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
           "  .logo { text-align: center; margin-bottom: 30px; }" +
           "  .logo img { max-width: 160px; height: auto; }" +
           "  h2 { color: #333333; }" +
           "  p { color: #555555; font-size: 16px; line-height: 1.6; }" +
           "  .button { display: inline-block; padding: 12px 24px; margin: 20px 0; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; transition: background-color 0.3s ease; }" +
           "  .button:hover { background-color: #0056b3; }" +
           "  .footer { font-size: 13px; color: #999999; margin-top: 30px; text-align: center; }" +
           "</style>" +
           "</head>" +
           "<body>" +
           "<div class=\"container\">" +
           "<div class=\"logo\">" +
           "<img src=\"https://github.com/bonoboprog/Chronogram/blob/Backend-patch-2/docs/Logo.png?raw=true\" alt=\"Chronogram Logo\">" +
           "</div>" +
           "<h2>Password Reset Request</h2>" +
           "<p>Hello,</p>" +
           "<p>We received a request to reset the password for your Chronogram account. If you did not request this, please ignore this email.</p>" +
           "<p>To set a new password, click the button below:</p>" +
           "<p style=\"text-align: center;\"><a href=\"" + resetUrl + "\" class=\"button\">Reset Your Password</a></p>" +
           "<p>This link will expire in 30 minutes.</p>" +
           "<p>Thank you,<br>The Chronogram Team</p>" +
           "<div class=\"footer\">" +
           "<p>This is an automated message. Please do not reply to this email.</p>" +
           "</div>" +
           "</div>" +
           "</body>" +
           "</html>";
    }


    /**
     * Crea l'oggetto Properties leggendo le configurazioni dalle variabili d'ambiente.
     * @return un oggetto Properties configurato per la sessione SMTP.
     */
    private Properties createMailProperties() {
        Properties props = new Properties();
        // Legge l'host e la porta dalle variabili d'ambiente
        String host = System.getenv("MAIL_HOST");
        String port = System.getenv("MAIL_PORT");

        if (host == null || port == null) {
            logger.fatal("FATAL: Environment variables MAIL_HOST and/or MAIL_PORT are not set!");
            throw new IllegalStateException("SMTP host/port are not configured.");
        }
        
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Per connessioni sicure
        return props;
    }
}
