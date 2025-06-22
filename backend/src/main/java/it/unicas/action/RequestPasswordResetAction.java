package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.PasswordResetService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestPasswordResetAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RequestPasswordResetAction.class);

    // --- Dipendenze ---
    private final PasswordResetService passwordResetService = new PasswordResetService();

    // --- INPUT ---
    private String email;

    // --- OUTPUT ---
    private boolean success;
    private String message;

    @Override
    public String execute() {
        logger.info("Password reset requested for email: {}", email);
        try {
            // Delega tutta la logica al service
            String rawToken = passwordResetService.initiatePasswordReset(email);

            if (rawToken != null) {
                // Se il service restituisce un token, significa che l'utente esiste e possiamo inviare l'email.
                String resetUrl = "http://localhost:3000/reset-password?token=" + rawToken;
                
                // TODO: Logica per inviare l'email con la resetUrl
                logger.info("SIMULATING EMAIL SEND to {}: Reset link is {}", email, resetUrl);
            }

            // Per motivi di sicurezza, la risposta al frontend è sempre la stessa,
            // che l'utente esista o meno.
            this.success = true;
            this.message = "If your email address exists in our system, you will receive a password reset link.";

        } catch (ValidationException e) {
            logger.warn("Invalid input for password reset request: {}", e.getMessage());
            // Anche in caso di validazione fallita, potremmo voler dare un messaggio generico
            this.success = false;
            this.message = e.getMessage();
        } catch (ServiceException e) {
            logger.error("A service error occurred while requesting password reset for {}", email, e);
            this.success = false;
            this.message = "An internal error occurred. Please try again later.";
        }
        return SUCCESS;
    }

    // --- Getters and Setters ---
    public void setEmail(String email) { this.email = email; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
