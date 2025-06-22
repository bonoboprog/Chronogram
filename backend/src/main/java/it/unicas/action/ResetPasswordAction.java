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

    // --- OUTPUT ---
    private boolean success;
    private String message;

    @Override
    public String execute() {
        logger.info("Attempting to reset password with provided token.");
        try {
            // Delega tutta la logica di reset al service
            passwordResetService.resetPassword(token, newPassword);

            this.success = true;
            this.message = "Password has been successfully reset.";
            logger.info("Password reset process completed successfully.");

        } catch (ValidationException e) {
            logger.warn("Password reset failed due to validation error: {}", e.getMessage());
            this.success = false;
            this.message = e.getMessage();
        } catch (ServiceException e) {
            logger.warn("Password reset failed due to service error: {}", e.getMessage());
            this.success = false;
            this.message = e.getMessage();
        } catch (Exception e) {
            logger.error("An unexpected internal error occurred during password reset.", e);
            this.success = false;
            this.message = "An internal server error occurred.";
        }
        return SUCCESS;
    }

    // --- Getters and Setters ---
    public void setToken(String token) { this.token = token; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
