package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.PasswordResetDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.PasswordResetDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ResetPasswordAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(ResetPasswordAction.class);

    private String token;
    private String newPassword;
    private boolean success;
    private String message;

    public void setToken(String token) {
        this.token = token;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String execute() {
        logger.info("Starting password reset process");

        if (isBlank(token)) {
            logger.warn("Password reset attempt with empty token");
            return fail("Invalid reset token.");
        }

        if (isBlank(newPassword) || newPassword.length() < 6) {
            logger.warn("Password reset attempt with invalid new password (length: {})",
                    newPassword != null ? newPassword.length() : "null");
            return fail("Invalid input. Password must be at least 6 characters.");
        }

        try (Connection conn = DBUtil.getConnection()) {
            logger.debug("Database connection established for password reset");

            PasswordResetDAO resetDAO = new PasswordResetDAO();
            logger.debug("Retrieving all active password reset tokens");
            List<PasswordResetDTO> candidates = resetDAO.getAllActiveResets(conn);
            logger.debug("Found {} active password reset tokens", candidates.size());

            PasswordResetDTO matchedReset = null;
            logger.debug("Searching for matching reset token");

            for (PasswordResetDTO dto : candidates) {
                if (BCrypt.checkpw(token, dto.getToken_hash())) {
                    matchedReset = dto;
                    logger.debug("Found matching reset token for user ID: {}", dto.getUserId());
                    break;
                }
            }

            if (matchedReset == null) {
                logger.warn("No matching reset token found (provided token: [REDACTED])");
                return fail("Reset token is invalid or has expired.");
            }

            if (matchedReset.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                logger.warn("Expired reset token attempted for user ID: {} (expired at: {})",
                        matchedReset.getUserId(), matchedReset.getExpirationTime());
                return fail("Reset token has expired.");
            }

            logger.debug("Generating new password hash");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(newPassword);

            logger.info("Updating password for user ID: {}", matchedReset.getUserId());
            UserAuthDAO userDAO = new UserAuthDAO();
            userDAO.updatePassword(matchedReset.getUserId(), hashedPassword, conn);

            logger.debug("Deleting used reset token for user ID: {}", matchedReset.getUserId());
            resetDAO.deleteToken(matchedReset.getToken_hash(), conn);

            success = true;
            message = "Password has been successfully reset.";
            logger.info("Password successfully reset for user ID: {}", matchedReset.getUserId());

        } catch (Exception e) {
            logger.error("Error during password reset process", e);
            return fail("An internal error occurred while resetting your password.");
        }

        return SUCCESS;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String fail(String msg) {
        this.success = false;
        this.message = msg;
        logger.warn("Password reset failed: {}", msg);
        return SUCCESS;
    }
}