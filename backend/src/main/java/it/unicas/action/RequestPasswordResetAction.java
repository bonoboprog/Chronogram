package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.PasswordResetDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.PasswordResetDTO;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class RequestPasswordResetAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RequestPasswordResetAction.class);

    private String email;
    private boolean success;
    private String message;

    public void setEmail(String email) { this.email = email; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String execute() {
        logger.info("Password reset requested for email: {}", email);

        if (isBlank(email)) {
            fail("Email is required.");
            logger.warn("Missing email input for password reset.");
            return SUCCESS;
        }

        try (Connection conn = DBUtil.getConnection()) {
            UserAuthDAO userAuthDAO = new UserAuthDAO();
            UserAuthDTO user = userAuthDAO.getUserAuthByEmail(email, conn);

            if (user == null) {
                // Avoid leaking whether the user exists
                success = true;
                message = "If your email is registered, you’ll receive a reset link.";
                logger.warn("Password reset requested for unknown email: {}", email);
                return SUCCESS;
            }

            if (user.getIsActive() == 0) {
                fail("Account is inactive. Please contact support.");
                logger.warn("Password reset attempted for inactive account: {}", email);
                return SUCCESS;
            }

            // Generate raw token (for email) and hash (for DB)
            String rawToken = UUID.randomUUID().toString();
            String tokenHash = BCrypt.hashpw(rawToken, BCrypt.gensalt());

            Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());
            Timestamp expiration = Timestamp.valueOf(LocalDateTime.now().plusMinutes(30));

            PasswordResetDTO resetDTO = new PasswordResetDTO();
            resetDTO.setUserId(user.getUserId());
            resetDTO.setToken_hash(tokenHash);
            resetDTO.setCreated_at(createdAt);
            resetDTO.setExpirationTime(expiration);

            PasswordResetDAO resetDAO = new PasswordResetDAO();
            resetDAO.insertResetToken(resetDTO, conn);





            // Compose the reset URL with the raw token





            String resetUrl = "https://yourapp.com/reset-password?token=" + rawToken;
            logger.info("Password reset link for {}: {}", email, resetUrl);
            // TODO: Actually send email with this resetUrl

            success = true;
            message = "If your email is registered, you’ll receive a reset link.";
        } catch (Exception e) {
            logger.error("Error processing password reset for {}: {}", email, e.getMessage(), e);
            fail("Internal server error. Please try again later.");
        }

        return SUCCESS;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void fail(String msg) {
        this.success = false;
        this.message = msg;
    }
}
