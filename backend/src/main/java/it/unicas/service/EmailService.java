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

            // OLD: NOT CONSIDER THIS COMMENTED SECTION!!!!!
            // Costruisce l'URL completo che l'utente cliccherà nell'email
            // NOTA: Assicurati che 'http://localhost:8100' sia l'indirizzo corretto del tuo frontend
            //String resetUrl = "http://localhost:8100/reset-password?token=" + token; 
           
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
        // Questo è un template HTML di base ma professionale. Puoi personalizzarlo come preferisci.
        return "<!DOCTYPE html>" +
               "<html lang=\"it\">" +
               "<head>" +
               "<meta charset=\"UTF-8\">" +
               "<style>" +
               " body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
               " .container { width: 90%; max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
               " .button { display: inline-block; padding: 10px 20px; margin: 20px 0; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; }" +
               " .footer { font-size: 0.9em; color: #777; margin-top: 20px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class=\"container\">" +
               "<h2>Richiesta di Reset Password</h2>" +
               "<p>Ciao,</p>" +
               "<p>Abbiamo ricevuto una richiesta per resettare la password del tuo account su Chronogram. Se non sei stato tu a richiederlo, puoi tranquillamente ignorare questa email.</p>" +
               "<p>Per procedere con la creazione di una nuova password, clicca sul pulsante qui sotto:</p>" +
               "<a href=\"" + resetUrl + "\" class=\"button\">Resetta la tua Password</a>" +
               "<p>Questo link scadrà tra 30 minuti.</p>" +
               "<p>Grazie,<br>Il Team di Chronogram</p>" +
               "<div class=\"footer\">" +
               "<p>Questo è un messaggio automatico, per favore non rispondere a questa email.</p>" +
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
