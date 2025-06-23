package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dto.RegistrationDTO;
import it.unicas.service.RegistrationService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the web request for user registration.
 * Delegates all business logic to the RegistrationService.
 */
public class RegistrationAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RegistrationAction.class);

    // --- Dipendenza dal Service Layer ---
    private final RegistrationService registrationService = new RegistrationService();

    // --- Campi del form (INPUT da Struts) ---
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private String birthday;
    private String gender;
    private String address; // <-- NUOVO CAMPO

    // --- Wrapper della risposta (OUTPUT per Struts) ---
    private RegistrationResponse registrationResponse;

    @Override
    public String execute() {
        logger.info("Registration request received for email: {}", email);

        // 1. Raccoglie i dati in un unico oggetto per passarli in modo pulito.
        RegistrationDTO data = new RegistrationDTO(
                name, surname, phone, email, password, birthday, gender, address // <-- AGGIORNATO
        );

        try {
            // 2. Delega tutta la logica di business a un singolo metodo del service.
            registrationService.registerNewUser(data);

            setSuccess("Registration successful!");
            logger.info("Successfully processed registration for {}", email);

        } catch (ValidationException e) {
            logger.warn("Registration failed for {} due to validation error: {}", email, e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Registration failed for {} due to a service error", email, e);
            if (e.getMessage() != null && e.getMessage().contains("Email already registered")) {
                setFailure("Email already registered.");
            } else {
                setFailure("Registration failed due to a system error.");
            }
        }

        return SUCCESS;
    }

    // --- Metodi helper per la risposta JSON ---
    private void setSuccess(String msg) {
        this.registrationResponse = new RegistrationResponse(true, msg);
    }

    private void setFailure(String msg) {
        this.registrationResponse = new RegistrationResponse(false, msg);
    }

    // --- DTO interno per la risposta JSON ---
    public static class RegistrationResponse {
        private final boolean success;
        private final String message;

        public RegistrationResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    // --- Getters e Setters per Struts2 ---
    public RegistrationResponse getRegistrationResponse() {
        return registrationResponse;
    }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; } // <-- NUOVO SETTER
}
