package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.service.LoginService;
import it.unicas.dto.LoginResultDTO; // <-- Importa il nuovo DTO
import it.unicas.service.exception.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Gestisce le richieste web di login.
 * Delega tutta la logica di business al LoginService e prepara la risposta JSON.
 */
public class LoginAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LoginAction.class);

    // --- Dipendenza dal Service Layer ---
    private final LoginService loginService = new LoginService();

    // --- INPUT (impostati da Struts) ---
    private String email;
    private String password;

    // --- OUTPUT (serializzati in JSON da Struts) ---
    private boolean success;
    private String message;
    private String username;
    private String token; // Il nostro nuovo JWT!

    @Override
    public String execute() {
        logger.info("Login attempt received for email: {}", email);
        try {
            // 1. Tutta la logica è delegata a un singolo metodo del service.
            LoginResultDTO result = loginService.loginUser(email, password);
            
            // 2. Se il service ha successo, popola i campi di output dall'oggetto risultato.
            this.success = true;
            this.message = "Login successful!";
            this.username = result.getUsername();
            this.token = result.getJwtToken(); // Aggiunge il token alla risposta
            
            logger.info("Login for {} successful. JWT generated.", email);
            
        } catch (AuthenticationException e) {
            // 3. Gestisce gli errori di autenticazione in modo pulito.
            logger.warn("Authentication failed for email {}: {}", email, e.getMessage());
            this.success = false;
            this.message = e.getMessage(); // Usa il messaggio specifico dall'eccezione (es. "Account is locked")
        } catch (Exception e) {
            // 4. Gestisce tutti gli altri errori imprevisti.
            logger.error("An unexpected error occurred during login for {}", email, e);
            this.success = false;
            this.message = "An internal server error occurred.";
        }
        return SUCCESS;
    }

    // --- Getters e Setters per tutti i campi INPUT e OUTPUT ---
    // Struts li usa per popolare i dati in entrata e per creare il JSON in uscita.

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }
    public String getToken() { return token; }
}
