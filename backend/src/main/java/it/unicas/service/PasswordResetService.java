package it.unicas.service;

// =========================================================================
// == AGGIUNTA NECESSARIA: IMPORT PER LE CLASSI NEL SOTTO-PACCHETTO ======
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
// =========================================================================

import it.unicas.dao.PasswordResetDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.PasswordResetDTO;
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

    private final UserAuthDAO userAuthDAO;
    private final PasswordResetDAO passwordResetDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordResetService() {
        this.userAuthDAO = new UserAuthDAO();
        this.passwordResetDAO = new PasswordResetDAO();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    // =========================================================================
    // == HO INCLUSO ANCHE IL METODO 'initiatePasswordReset' E L'HELPER   ======
    // == CHE TI HO SUGGERITO NELLA RISPOSTA PRECEDENTE, COSÌ HAI TUTTO   ======
    // == IL CODICE COMPLETO E FUNZIONANTE IN UN UNICO POSTO.             ======
    // =========================================================================

    public String initiatePasswordReset(String email) throws ServiceException, ValidationException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new ValidationException("A valid email is required.");
        }

        try (Connection conn = DBUtil.getConnection()) {
            UserAuthDTO user = userAuthDAO.getUserAuthByEmail(email, conn);

            if (user == null || user.getIsActive() == 0) {
                logger.warn("Password reset initiated for a non-existent or inactive user: {}", email);
                return null;
            }

            String selector = generateSecureString(16);
            String verifier = generateSecureString(32);
            String verifierHash = passwordEncoder.encode(verifier);

            PasswordResetDTO resetDTO = new PasswordResetDTO();
            resetDTO.setUserId(user.getUserId());
            resetDTO.setSelector(selector);
            resetDTO.setVerifierHash(verifierHash);
            resetDTO.setExpirationTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)));
            resetDTO.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            // Nota: qui ci vorrà il nuovo metodo save() nel DAO
            passwordResetDAO.save(resetDTO, conn);

            return selector + ":" + verifier;

        } catch (SQLException e) {
            logger.error("Database error during password reset initiation for {}", email, e);
            throw new ServiceException("An internal database error occurred.", e);
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

            // Nota: qui ci vorrà il nuovo metodo findBySelector() nel DAO
            PasswordResetDTO resetDTO = passwordResetDAO.findBySelector(selector, conn);
            
            if (resetDTO == null || resetDTO.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            if (!passwordEncoder.matches(verifier, resetDTO.getVerifierHash())) {
                throw new ServiceException("Reset token is invalid or has expired.");
            }

            logger.info("Token verified for user ID: {}. Proceeding to reset password.", resetDTO.getUserId());
            String newPasswordHash = passwordEncoder.encode(newPassword);
            userAuthDAO.updatePassword(resetDTO.getUserId(), newPasswordHash, conn);
            
            // Nota: qui ci vorrà il nuovo metodo deleteBySelector() nel DAO
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
