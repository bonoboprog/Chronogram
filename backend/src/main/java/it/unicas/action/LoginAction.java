package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LoginAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    private String email;
    private String password;
    private boolean success;
    private String message;
    private String username;

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }

    @Override
    public String execute() {
        logger.info("Login attempt for email: {}", email);

        if (isBlank(email) || isBlank(password)) {
            fail("Email and password are required.");
            logger.warn("Missing credentials");
            return SUCCESS;
        }

        try (Connection conn = DBUtil.getConnection()) {
            UserAuthDAO userAuthDAO = new UserAuthDAO();
            UserAuthDTO userAuth = userAuthDAO.getUserAuthByEmail(email, conn);

            if (userAuth == null) {
                fail("Invalid credentials.");
                logger.warn("No account found for email: {}", email);
                return SUCCESS;
            }

            if (userAuth.getIsActive() == 0) {
                fail("Account is inactive. Please contact support.");
                logger.warn("Inactive account: {}", email);
                return SUCCESS;
            }

            Timestamp lockedUntil = userAuth.getLockedUntil();
            if (lockedUntil != null && lockedUntil.after(Timestamp.valueOf(LocalDateTime.now()))) {
                fail("Account is locked. Please try again later.");
                logger.warn("Account locked until {} for email: {}", lockedUntil, email);
                return SUCCESS;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, userAuth.getPasswordHash())) {
                success = true;
                message = "Login successful!";
                username = userAuth.getUsername();
                logger.info("Login success for {}", email);

                userAuthDAO.updateLastLogin(email, conn); // reset attempts and set last_login
            } else {
                int failedAttempts = userAuth.getFailedLoginAttempts() + 1;
                Timestamp newLockedUntil = null;

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    newLockedUntil = Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                    message = "Too many failed login attempts. Account locked.";
                    logger.warn("Account {} locked due to {} failed attempts", email, failedAttempts);
                } else {
                    message = "Invalid credentials.";
                    logger.warn("Wrong password for {}", email);
                }

                success = false;
                userAuthDAO.updateLoginAttemptsAndLockout(email, failedAttempts, newLockedUntil, conn);
            }

        } catch (Exception e) {
            logger.error("Login error for email {}: {}", email, e.getMessage(), e);
            fail("System error during login.");
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
