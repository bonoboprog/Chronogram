package it.unicas.service;

import it.unicas.dao.UserAuthDAO;
import it.unicas.dao.UserDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import it.unicas.dto.UserDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import it.unicas.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * Gestisce la logica di business per la registrazione di un nuovo utente.
 */
public class RegistrationService {

    private static final Logger logger = LogManager.getLogger(RegistrationService.class);

    private final UserAuthDAO authDAO;
    private final UserDAO userDAO;

    public RegistrationService() {
        this.authDAO = new UserAuthDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Registra un nuovo utente nel sistema in un'unica transazione.
     * @param data L'oggetto contenente tutti i dati dal form di registrazione.
     * @throws ValidationException se i dati non sono validi.
     * @throws ServiceException se si verifica un errore di business (es. email duplicata) o di sistema.
     */
    public void registerNewUser(RegistrationData data) throws ValidationException, ServiceException {
        // 1. Validazione approfondita dei dati
        validateRegistrationData(data);

        // 2. Preparazione dei DTO
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        UserAuthDTO authDTO = new UserAuthDTO();
        authDTO.setEmail(data.email());
        authDTO.setPasswordHash(PasswordUtil.getInstance().encode(data.password()));
        authDTO.setUsername(data.name() + " " + data.surname());
        authDTO.setCreatedAt(now);
        authDTO.setUpdatedAt(now);
        authDTO.setIsActive(1);

        UserDTO userDTO = new UserDTO();
        userDTO.setGender(data.gender());
        userDTO.setAddress(data.phone()); // (Mantenuto come placeholder come nel tuo codice)
        userDTO.setBirthday(parseBirthday(data.birthday()));
        userDTO.setCreatedAt(now);
        userDTO.setUpdatedAt(now);
        
        // 3. Blocco transazionale
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Controllo di unicità dell'email all'interno della transazione
                if (authDAO.getUserAuthByEmail(data.email(), conn) != null) {
                    throw new ServiceException("Email already registered.");
                }

                int userId = authDAO.insertUserAuth(authDTO, conn);
                if (userId == -1) {
                    throw new ServiceException("Could not create authentication record.");
                }

                userDTO.setUserId(userId);
                if (userDAO.insertUser(userDTO, conn) == -1) {
                    throw new ServiceException("Could not create user profile.");
                }

                conn.commit(); // Successo! Conferma la transazione
                logger.info("User {} registered successfully with user_id={}", data.email(), userId);

            } catch (Exception e) {
                conn.rollback(); // Se qualcosa va storto, annulla tutto
                logger.warn("Registration transaction rolled back for email {}", data.email(), e);
                // Rilancia l'eccezione per farla gestire all'Action
                throw new ServiceException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error("Failed to get DB connection for registration", e);
            throw new ServiceException("Registration failed due to a system error.", e);
        }
    }

    private void validateRegistrationData(RegistrationData data) throws ValidationException {
        if (data.name() == null || data.name().trim().isEmpty() ||
            data.surname() == null || data.surname().trim().isEmpty() ||
            data.email() == null || data.email().trim().isEmpty() ||
            data.password() == null || data.password().trim().isEmpty()) {
            throw new ValidationException("All required fields must be filled.");
        }

        // Aggiungi qui le altre validazioni (complessità password, formato email, etc.)
        if (!data.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            throw new ValidationException("Invalid email format.");
        }
        if (data.password().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
    }
    
    // Puoi mantenere questo helper qui o spostarlo in una classe di utility
    private Date parseBirthday(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            fmt.setLenient(false);
            return new Date(fmt.parse(str).getTime());
        } catch (ParseException e) {
            logger.warn("Invalid birthday format '{}'", str);
            return null;
        }
    }
}
