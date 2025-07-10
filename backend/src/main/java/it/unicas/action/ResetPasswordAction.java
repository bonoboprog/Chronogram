package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.PasswordResetService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResetPasswordAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(ResetPasswordAction.class);

    // --- Dipendenze ---
    private final PasswordResetService passwordResetService = new PasswordResetService();

    // --- INPUT ---
    private String token;
    private String newPassword;

    // --- Wrapper della risposta (OUTPUT per Struts) ---
    private ResetPasswordResponse resetPasswordResponse; // <-- NUOVO CAMPO PER LA RISPOSTA

    @Override
    public String execute() {
        logger.info("Attempting to reset password with provided token.");
        try {
            // Delega tutta la logica di reset al service
            passwordResetService.resetPassword(token, newPassword);

            setSuccess("Password has been successfully reset.");
            logger.info("Password reset process completed successfully.");

        } catch (ValidationException e) {
            logger.warn("Password reset failed due to validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.warn("Password reset failed due to service error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected internal error occurred during password reset.", e);
            setFailure("An internal server error occurred. Please try again later."); // Messaggio più amichevole
        }

        // --- START: DEBUGGING BLOCK ---
        if (this.resetPasswordResponse != null) {
            logger.debug("Returning JSON with success={} and message='{}'",
                    this.resetPasswordResponse.isSuccess(),
                    this.resetPasswordResponse.getMessage());
        } else {
            logger.error("FATAL: resetPasswordResponse object is null before returning result. No JSON will be generated correctly.");
        }
        // --- END: DEBUGGING BLOCK ---

        return SUCCESS;
    }

    // --- Metodi helper per la risposta JSON ---
    private void setSuccess(String msg) {
        this.resetPasswordResponse = new ResetPasswordResponse(true, msg);
    }

    private void setFailure(String msg) {
        this.resetPasswordResponse = new ResetPasswordResponse(false, msg);
    }

    // --- DTO interno per la risposta JSON ---
    public static class ResetPasswordResponse {
        private final boolean success;
        private final String message;

        public ResetPasswordResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success;}
        public String getMessage() { return message;}
    }

    // --- Getters and Setters ---
    public void setToken(String token) { this.token = token; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    // Rinomina il getter per la risposta per coerenza con le altre azioni
    public ResetPasswordResponse getResetPasswordResponse() {
        return resetPasswordResponse;
    }
}