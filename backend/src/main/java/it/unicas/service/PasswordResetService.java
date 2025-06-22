package it.unicas.service;

import it.unicas.dao.PasswordResetDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.PasswordResetDTO;
import it.unicas.dto.UserAuthDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import it.unicas.util.PasswordUtil; // <-- IMPORT per il singleton
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Gestisce la logica di business per il processo di reset password.
 * Orchestra le operazioni sui DAO e garantisce l'atomicità tramite transazioni.
 */
public class PasswordResetService {

    private static final Logger logger = LogManager.getLogger(PasswordResetService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    // --- Le dipendenze del service ---
    private final UserAuthDAO userAuthDAO;
    private final PasswordResetDAO passwordResetDAO;
    private final EmailService emailService; // <-- 1. AGGIUNTA LA DIPENDENZA EMAILSERVICE

    public PasswordResetService() {
        // Istanziazione manuale in assenza di un framework di Dependency Injection
        this.userAuthDAO = new UserAuthDAO();
        this.passwordResetDAO = new PasswordResetDAO();
        this.emailService = new EmailService(); // <-- Istanzia il nuovo service
    }

    public String initiatePasswordReset(String email) throws ServiceException, ValidationException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new ValidationException("A valid email is required.");
        }

        // 2. GESTIONE MANUALE DELLA TRANSAZIONE
        // Per garantire che salvataggio su DB e invio email siano atomici
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // INIZIO TRANSAZIONE

            UserAuthDTO user = userAuthDAO.getUserAuthByEmail(email, conn);

            if (user == null || user.getIsActive() == 0) {
                logger.warn("Password reset initiated for a non-existent or inactive user: {}", email);
                conn.commit(); // Confermiamo la transazione (che non ha fatto nulla) e usciamo
                return null;
            }

            String selector = generateSecureString(16);
            String verifier = generateSecureString(32);
            String verifierHash = PasswordUtil.getInstance().encode(verifier); // <-- 4. USO DEL SINGLETON

            PasswordResetDTO resetDTO = new PasswordResetDTO();
            resetDTO.setUserId(user.getUserId());
            resetDTO.setSelector(selector);
            resetDTO.setVerifierHash(verifierHash);
            resetDTO.setExpirationTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)));
            resetDTO.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            passwordResetDAO.save(resetDTO, conn); // Salva il token nel DB

            String tokenCompleto = selector + ":" + verifier;

            // 3. CHIAMATA EFFETTIVA ALL'INVIO EMAIL
            // Se questo metodo fallisce, lancia un'eccezione e la transazione verrà annullata.
            emailService.sendPasswordResetEmail(email, tokenCompleto);

            conn.commit(); // FINE TRANSAZIONE (tutto è andato a buon fine)
            logger.info("Reset token created and email sent successfully for {}", email);
            return tokenCompleto;

        } catch (Exception e) {
            rollback(conn); // Se qualcosa va storto (DB o email), annulla tutto
            // Rilanciamo un'eccezione generica del service
            logger.error("Error during password reset initiation for {}. Transaction rolled back.", email, e);
            throw new ServiceException("An internal error occurred while initiating the password reset.", e);
        } finally {
            closeConnection(conn);
        }
    }


    public void resetPassword(String token, String newPassword) throws ValidationException, ServiceException {
        validateInput(token, newPassword);

        String[] parts = token.split(":");
        if (parts.length != 2) {
            throw new ValidationException("Invalid token format.");
        }
        String selector = parts[0];
        String verifier = parts[1];

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            PasswordResetDTO resetDTO = passwordResetDAO.findBySelector(selector, conn);
            
            if (resetDTO == null || resetDTO.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            if (!PasswordUtil.getInstance().matches(verifier, resetDTO.getVerifierHash())) { // <-- 4. USO DEL SINGLETON
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            logger.info("Token verified for user ID: {}. Proceeding to reset password.", resetDTO.getUserId());
            String newPasswordHash = PasswordUtil.getInstance().encode(newPassword); // <-- 4. USO DEL SINGLETON
            userAuthDAO.updatePassword(resetDTO.getUserId(), newPasswordHash, conn);
            
            passwordResetDAO.deleteBySelector(selector, conn);

            conn.commit();
            logger.info("Password successfully reset and transaction committed for user ID: {}", resetDTO.getUserId());

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error during password reset process. Transaction rolled back.", e);
            throw new ServiceException("An internal database error occurred.", e);
        } catch (Exception e) {
            rollback(conn);
            throw e;
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
    }
    
    private String generateSecureString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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
