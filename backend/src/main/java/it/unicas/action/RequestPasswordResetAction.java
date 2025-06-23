package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.PasswordResetService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest; // <-- 1. AGGIUNTO IMPORT
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware; // <-- 2. AGGIUNTO IMPORT

/**
 * Gestisce la richiesta iniziale di reset password.
 * Implementa ServletRequestAware per accedere agli header della richiesta HTTP.
 */
public class RequestPasswordResetAction extends ActionSupport implements ServletRequestAware { // <-- 3. IMPLEMENTA L'INTERFACCIA

    private static final Logger logger = LogManager.getLogger(RequestPasswordResetAction.class);

    // --- Dipendenze ---
    private final PasswordResetService passwordResetService = new PasswordResetService();

    // --- INPUT ---
    private String email;

    // --- OGGETTO INIETTATO DA STRUTS ---
    private HttpServletRequest request; // <-- 4. AGGIUNTO CAMPO PER LA RICHIESTA HTTP

    // --- OUTPUT ---
    private boolean success;
    private String message;

    @Override
    public String execute() {
        logger.info("Password reset requested for email: {}", email);
        try {
            // === INIZIO MODIFICA ===
            // 5. Estrai l'header 'Origin' dalla richiesta HTTP.
            // Questo ci dice da quale URL è partita la chiamata del frontend (es. http://localhost:8100)
            String origin = request.getHeader("Origin");
            logger.debug("Request received from origin: {}", origin);

            // 6. Passa sia l'email che l'origine al service.
            passwordResetService.initiatePasswordReset(email, origin);
            // === FINE MODIFICA ===

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

    /**
     * Metodo richiesto dall'interfaccia ServletRequestAware.
     * Struts 2 lo chiamerà automaticamente per iniettare l'oggetto della richiesta HTTP.
     * @param request l'oggetto HttpServletRequest della chiamata corrente.
     */
    @Override
    public void setServletRequest(HttpServletRequest request) { // <-- 7. AGGIUNTO METODO SETTER
        this.request = request;
    }

    // --- Getters and Setters (rimangono invariati) ---
    public void setEmail(String email) { this.email = email; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
