package it.unicas.service;

import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.LoginResultDTO;
import it.unicas.dto.UserAuthDTO;
import it.unicas.service.exception.AuthenticationException;
import it.unicas.util.JwtUtil;
import it.unicas.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Gestisce la logica di business per il processo di login,
 * inclusa la protezione dalla forza bruta e la generazione di JWT.
 * Utilizza la gestione esplicita delle transazioni.
 */
public class LoginService {
    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private final UserAuthDAO userAuthDAO;

    public LoginService() {
        this.userAuthDAO = new UserAuthDAO();
    }

    /**
     * Esegue l'autenticazione di un utente, gestendo l'intera operazione
     * all'interno di un'unica transazione di database.
     * @param email L'email fornita per il login.
     * @param password La password in chiaro fornita.
     * @return Un oggetto LoginResultDTO contenente username e token JWT in caso di successo.
     * @throws AuthenticationException se l'autenticazione fallisce per qualsiasi motivo.
     */
    public LoginResultDTO loginUser(String email, String password) throws AuthenticationException {
        // Validazione preliminare dei dati di input
        validateInput(email, password);

        // Gestione manuale della connessione per un controllo transazionale esplicito
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // INIZIA LA TRANSAZIONE

            UserAuthDTO user = userAuthDAO.getUserAuthByEmail(email, conn);

            // Controlla se l'utente esiste ed è attivo
            if (user == null || user.getIsActive() == 0) {
                logger.warn("Login attempt for non-existent or inactive user: {}", email);
                throw new AuthenticationException("Invalid credentials.");
            }

            // Controlla se l'account è bloccato temporalmente
            Timestamp lockedUntil = user.getLockedUntil();
            if (lockedUntil != null && lockedUntil.after(Timestamp.valueOf(LocalDateTime.now()))) {
                logger.warn("Login attempt on locked account for user: {}", email);
                throw new AuthenticationException("Account is locked. Please try again later.");
            }

            // Verifica la corrispondenza della password
            if (PasswordUtil.getInstance().matches(password, user.getPasswordHash())) {
                // Successo: azzera i tentativi falliti e aggiorna l'ultimo login
                logger.info("Login successful for {}", email);
                userAuthDAO.resetLoginAttempts(user.getUserId(), conn);
                conn.commit(); // Conferma la transazione

                String jwtToken = JwtUtil.generateToken(email);
                return new LoginResultDTO(user.getEmail(), jwtToken);

            } else {
                // Fallimento: incrementa i tentativi e gestisce il blocco dell'account
                logger.warn("Wrong password for {}", email);
                int newAttempts = user.getFailedLoginAttempts() + 1;
                Timestamp newLockout = null;
                String message = "Invalid credentials.";

                if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                    newLockout = Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                    message = "Account locked due to too many failed attempts.";
                    logger.warn("Account for {} has been locked.", email);
                }

                userAuthDAO.updateFailedLoginAttempt(user.getUserId(), newAttempts, newLockout, conn);
                conn.commit(); // Conferma la transazione

                throw new AuthenticationException(message);
            }
        } catch (SQLException e) {
            rollback(conn); // Se si verifica un errore SQL, annulla la transazione
            logger.error("Database error during login for {}", email, e);
            throw new AuthenticationException("A system error occurred during login.", e);
        } finally {
            closeConnection(conn); // Assicura che la connessione venga sempre chiusa
        }
    }

    private void validateInput(String email, String password) throws AuthenticationException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                logger.warn("Transaction is being rolled back due to an error.");
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Failed to rollback transaction", ex);
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("Failed to close connection", ex);
            }
        }
    }
}
