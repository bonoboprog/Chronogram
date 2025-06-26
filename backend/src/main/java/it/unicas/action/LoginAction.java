package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.LoginService;
import it.unicas.dto.LoginResultDTO;
import it.unicas.service.exception.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Gestisce le richieste web di login.
 * Delega tutta la logica di business al LoginService e prepara la risposta JSON.
 */
public class LoginAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    private final LoginService loginService = new LoginService();

    // --- INPUT da Struts ---
    private String email;
    private String password;

    // --- OUTPUT wrapper ---
    private LoginResponse loginResponse;

    @Override
    public String execute() {
        logger.info("Login attempt received for email: {}", email);

        try {
            LoginResultDTO result = loginService.loginUser(email, password);
            setSuccess("Login successful!", result.getUsername(), result.getJwtToken());
            logger.info("Login for {} successful. JWT generated.", email);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email {}: {}", email, e.getMessage());
            setFailure(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for {}", email, e);
            setFailure("An internal server error occurred.");
        }

        // --- DEBUG LOG ---
        if (loginResponse != null) {
            logger.debug("Returning JSON with success={} and message='{}'",
                    loginResponse.isSuccess(), loginResponse.getMessage());
        } else {
            logger.error("FATAL: loginResponse is null before returning result.");
        }

        return SUCCESS;
    }

    // --- Metodi helper ---
    private void setSuccess(String msg, String username, String token) {
        this.loginResponse = new LoginResponse(true, msg, username, token);
    }

    private void setFailure(String msg) {
        this.loginResponse = new LoginResponse(false, msg, null, null);
    }

    // --- DTO interno per la risposta JSON ---
    public static class LoginResponse {
        private final boolean success;
        private final String message;
        private final String username;
        private final String token;

        public LoginResponse(boolean success, String message, String username, String token) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.token = token;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUsername() { return username; }
        public String getToken() { return token; }
    }

    // --- Getter per la risposta JSON (usato da Struts) ---
    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    // --- Setters per input da Struts ---
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
