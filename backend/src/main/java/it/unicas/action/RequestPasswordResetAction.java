package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.PasswordResetService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * Gestisce la richiesta iniziale di reset password.
 * Implementa ServletRequestAware per accedere agli header della richiesta HTTP.
 */
public class RequestPasswordResetAction extends ActionSupport implements ServletRequestAware {

    private static final Logger logger = LogManager.getLogger(RequestPasswordResetAction.class);

    // --- Dipendenze ---
    private final PasswordResetService passwordResetService = new PasswordResetService();

    // --- INPUT ---
    private String email;

    // --- OGGETTO INIETTATO DA STRUTS ---
    private HttpServletRequest request;

    // --- Wrapper della risposta (OUTPUT per Struts) ---
    private PasswordResetResponse passwordResetResponse; // <-- NUOVO CAMPO PER LA RISPOSTA

    @Override
    public String execute() {
        logger.info("Password reset requested for email: {}", email);
        try {
            String origin = request.getHeader("Origin");
            logger.debug("Request received from origin: {}", origin);

            passwordResetService.initiatePasswordReset(email, origin);

            // La logica di business è completata. Impostiamo sempre lo stesso messaggio
            // di successo per non rivelare se un'email esiste o meno nel sistema.
            setSuccess("If your email address exists in our system, you will receive a password reset link.");

        } catch (ValidationException e) {
            // Se l'input non è valido (es. email vuota), restituisci un errore chiaro.
            logger.warn("Invalid input for password reset request: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            // Se c'è un errore interno (DB, invio email, ecc.), restituisci un errore generico.
            logger.error("A service error occurred while requesting password reset for {}", email, e);
            setFailure("An internal error occurred. Please try again later.");
        }

        // --- START: DEBUGGING BLOCK ---
        if (this.passwordResetResponse != null) {
            logger.debug("Returning JSON with success={} and message='{}'",
                    this.passwordResetResponse.isSuccess(),
                    this.passwordResetResponse.getMessage());
        } else {
            logger.error("FATAL: passwordResetResponse object is null before returning result. No JSON will be generated correctly.");
        }
        // --- END: DEBUGGING BLOCK ---

        return SUCCESS;
    }

    /**
     * Metodo richiesto dall'interfaccia ServletRequestAware.
     * Struts 2 lo chiamerà automaticamente per iniettare l'oggetto della richiesta HTTP.
     * @param request l'oggetto HttpServletRequest della chiamata corrente.
     */
    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    // --- Metodi helper per la risposta JSON ---
    private void setSuccess(String msg) {
        this.passwordResetResponse = new PasswordResetResponse(true, msg);
    }

    private void setFailure(String msg) {
        this.passwordResetResponse = new PasswordResetResponse(false, msg);
    }

    // --- DTO interno per la risposta JSON ---
    public static class PasswordResetResponse {
        private final boolean success;
        private final String message;

        public PasswordResetResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success;}
        public String getMessage() { return message;}
    }

    // --- Getters and Setters ---
    public void setEmail(String email) { this.email = email; }

    // Rinomina il getter per la risposta per coerenza con RegistrationAction
    public PasswordResetResponse getPasswordResetResponse() {
        return passwordResetResponse;
    }
}