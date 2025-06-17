package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt; // For password verification

import java.sql.Timestamp; // For Timestamp
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; // For adding time to lockout

public class LoginAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    // Properties to bind form data from frontend
    private String email;
    private String password;

    // Response properties for frontend
    private boolean success;
    private String message;
    private String username;

    // Constants for lockout policy
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // Getters and Setters for form properties
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Getters for response properties
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }

    @Override
    public String execute() {
        logger.info("Login attempt for email/username: {}", email);

        // 1. Server-Side Validation (Basic)
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            this.success = false;
            this.message = "Email/Username and password are required.";
            logger.warn("Validation failed: Missing credentials for login.");
            return SUCCESS;
        }

        // 2. Retrieve Stored Hash and UserAuthDTO
        UserAuthDAO userAuthDAO = new UserAuthDAO();
        UserAuthDTO userAuth = userAuthDAO.getUserAuthByEmail(email);

        if (userAuth == null) {
            this.success = false;
            this.message = "Invalid credentials."; // Generic message for security
            logger.warn("Login failed: User not found for email/username: {}", email);
            return SUCCESS;
        }

        // 3. Check Account Status (is_active, locked_until)
        if (userAuth.getIsActive() == 0) {
            this.success = false;
            this.message = "Account is inactive. Please contact support.";
            logger.warn("Login failed: Account for {} is inactive.", email);
            return SUCCESS;
        }

        Timestamp lockedUntil = userAuth.getLockedUntil();
        if (lockedUntil!= null && lockedUntil.after(Timestamp.valueOf(LocalDateTime.now()))) {
            this.success = false;
            this.message = "Account is locked. Please try again later.";
            logger.warn("Login failed: Account for {} is locked until {}.", email, lockedUntil);
            return SUCCESS;
        }

        String storedPasswordHash = userAuth.getPasswordHash(); // Corrected method call

        // 4. Password Verification with BCrypt
        if (BCrypt.checkpw(password, storedPasswordHash)) {
            // Password matches
            this.success = true;
            this.message = "Login successful!";
            this.username = userAuth.getUsername(); // Set username for frontend
            logger.info("User {} logged in successfully.", email);

            // 5. Update Last Login and Reset Failed Attempts
            userAuthDAO.updateLastLogin(email); // This method will now reset failed_login_attempts and locked_until
        } else {
            // Password does not match
            this.success = false;
            this.message = "Invalid credentials."; // Generic message for security
            logger.warn("Login failed: Incorrect password for email/username: {}", email);

            // 6. Handle Failed Login Attempts
            int currentFailedAttempts = userAuth.getFailedLoginAttempts() + 1;
            Timestamp newLockedUntil = null;

            if (currentFailedAttempts >= MAX_FAILED_ATTEMPTS) {
                newLockedUntil = Timestamp.valueOf(LocalDateTime.now().plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES));
                this.message = "Too many failed login attempts. Account locked. Please try again later.";
                logger.warn("Account for {} locked due to {} failed attempts.", email, currentFailedAttempts);
            }

            userAuthDAO.updateLoginAttemptsAndLockout(email, currentFailedAttempts, newLockedUntil);
        }

        return SUCCESS;
    }
}
