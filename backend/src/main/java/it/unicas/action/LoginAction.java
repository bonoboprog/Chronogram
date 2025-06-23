package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.LoginService;
import it.unicas.service.LoginService.LoginResult;
import it.unicas.service.exception.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    private final LoginService loginService = new LoginService();

    // INPUT
    private String email;
    private String password;

    // OUTPUT
    private boolean success;
    private String message;
    private String username;
    private String token; // Il nostro nuovo JWT!

    @Override
    public String execute() {
        try {
            LoginResult result = loginService.loginUser(email, password);
            
            this.success = true;
            this.message = "Login successful!";
            this.username = result.username();
            this.token = result.jwtToken();
            
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email {}: {}", email, e.getMessage());
            this.success = false;
            this.message = e.getMessage();
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for {}", email, e);
            this.success = false;
            this.message = "An internal server error occurred.";
        }
        return SUCCESS;
    }

    // --- Getters e Setters per tutti i campi INPUT e OUTPUT ---
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }
    public String getToken() { return token; }
}
