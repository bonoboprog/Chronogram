package it.unicas.service;

import it.unicas.dao.PasswordResetDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.PasswordResetDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Gestisce la logica di business per il processo di reset password.
 * Orchestra le operazioni sui DAO e garantisce l'atomicità tramite transazioni.
 */
public class PasswordResetService {

    private static final Logger logger = LogManager.getLogger(PasswordResetService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    // Le dipendenze del service. In un'app Spring, sarebbero iniettate (@Autowired).
    private final UserAuthDAO userAuthDAO;
    private final PasswordResetDAO passwordResetDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordResetService() {
        // Istanziazione manuale in assenza di un framework di Dependency Injection
        this.userAuthDAO = new UserAuthDAO();
        this.passwordResetDAO = new PasswordResetDAO();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Metodo principale che esegue l'intero processo di reset password.
     * @param token Il token completo (selector:verifier) ricevuto dall'utente.
     * @param newPassword La nuova password in chiaro.
     * @throws ValidationException se l'input non è valido (es. password troppo corta).
     * @throws ServiceException se la logica di business fallisce (es. token non trovato o scaduto).
     */
    public void resetPassword(String token, String newPassword) throws ValidationException, ServiceException {
        // 1. Validazione preliminare dell'input
        validateInput(token, newPassword);

        // 2. Parsing del token per ottenere selector e verifier
        String[] parts = token.split(":");
        if (parts.length != 2) {
            throw new ValidationException("Invalid token format.");
        }
        String selector = parts[0];
        String verifier = parts[1];

        // 3. Blocco transazionale per garantire l'atomicità
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // INIZIO TRANSAZIONE

            // 4. Cerca il token nel DB usando il selettore
            // La query nel DAO dovrebbe anche controllare la scadenza (expiration_time > NOW())
            PasswordResetDTO resetDTO = passwordResetDAO.findBySelector(selector, conn);
            if (resetDTO == null) {
                // Non dare informazioni specifiche per sicurezza (evita di dire "il token è scaduto" vs "non esiste")
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            // 5. Verifica che il token non sia scaduto (doppio controllo)
            if (resetDTO.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            // 6. Verifica la parte segreta del token (verifier)
            if (!passwordEncoder.matches(verifier, resetDTO.getVerifierHash())) {
                // Stesso messaggio di errore per non rivelare quale parte del token era sbagliata
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            // 7. Se tutte le verifiche passano, procedi con l'aggiornamento
            logger.info("Token verified for user ID: {}. Proceeding to reset password.", resetDTO.getUserId());

            // Hash della nuova password
            String newPasswordHash = passwordEncoder.encode(newPassword);

            // Aggiorna la password dell'utente
            userAuthDAO.updatePassword(resetDTO.getUserId(), newPasswordHash, conn);

            // Invalida/elimina il token usato per prevenire il riutilizzo
            passwordResetDAO.deleteBySelector(selector, conn);

            conn.commit(); // FINE TRANSAZIONE (successo)
            logger.info("Password successfully reset and transaction committed for user ID: {}", resetDTO.getUserId());

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error during password reset process. Transaction rolled back.", e);
            throw new ServiceException("An internal database error occurred.", e);
        } catch (Exception e) {
            // Cattura ValidationException e ServiceException per fare il rollback
            rollback(conn);
            throw e; // Rilancia l'eccezione originale
        } finally {
            closeConnection(conn);
        }
    }

    private void validateInput(String token, String newPassword) throws ValidationException {
        if (token == null || token.trim().isEmpty()) {
            throw new ValidationException("Reset token cannot be empty.");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        // Qui si potrebbero aggiungere altri controlli sulla complessità della password
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                logger.warn("Transaction is being rolled back.");
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
