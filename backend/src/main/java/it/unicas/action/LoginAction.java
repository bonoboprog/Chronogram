package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt; // For password verification

public class LoginAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    // Properties to bind form data from frontend
    private String email; // Can be username or email depending on frontend implementation
    private String password;

    // Response properties for frontend
    private boolean success;
    private String message;
    private String username; // To return to frontend upon successful login

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

        String storedPasswordHash = userAuth.getPasswordHash();

        // 3. Password Verification with BCrypt
        if (BCrypt.checkpw(password, storedPasswordHash)) {
            // Password matches
            this.success = true;
            this.message = "Login successful!";
            this.username = userAuth.getUsername(); // Set username for frontend
            logger.info("User {} logged in successfully.", email);

            // 4. Update Last Login
            userAuthDAO.updateLastLogin(email);
        } else {
            // Password does not match
            this.success = false;
            this.message = "Invalid credentials."; // Generic message for security
            logger.warn("Login failed: Incorrect password for email/username: {}", email);
        }

        return SUCCESS;
    }
}