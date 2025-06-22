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
            // Delega tutta la logica al service.
            // La chiamata a questo metodo ora crea il token, lo salva E INVIA L'EMAIL.
            passwordResetService.initiatePasswordReset(email);

            // La logica di business è completata. Impostiamo sempre lo stesso messaggio
            // di successo per non rivelare se un'email esiste o meno nel sistema.
            this.success = true;
            this.message = "If your email address exists in our system, you will receive a password reset link.";

        } catch (ValidationException e) {
            // Se l'input non è valido (es. email vuota), restituisci un errore chiaro.
            logger.warn("Invalid input for password reset request: {}", e.getMessage());
            this.success = false;
            this.message = e.getMessage();
        } catch (ServiceException e) {
            // Se c'è un errore interno (DB, invio email, ecc.), restituisci un errore generico.
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
