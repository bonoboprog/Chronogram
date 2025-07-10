package it.unicas.dao;

import it.unicas.dto.PasswordResetDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO per la gestione dei token di reset password.
 * Aggiornato per utilizzare il pattern Selector/Verifier.
 */
public class PasswordResetDAO {

    private static final Logger logger = LogManager.getLogger(PasswordResetDAO.class);

    /**
     * Salva un nuovo record di reset password nel database.
     * Utilizza "ON DUPLICATE KEY UPDATE" per gestire il caso in cui un utente richieda
     * un nuovo token prima che il vecchio sia scaduto, sovrascrivendolo.
     * Questo richiede un UNIQUE index sulla colonna `user_id`.
     * @param dto Il DTO contenente i dati del token (con selector e verifierHash).
     * @param conn La connessione al database.
     * @throws SQLException in caso di errore DB.
     */
    public void save(PasswordResetDTO dto, Connection conn) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, selector, verifier_hash, expiration_time, created_at) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE selector = VALUES(selector), verifier_hash = VALUES(verifier_hash), " +
                     "expiration_time = VALUES(expiration_time), created_at = VALUES(created_at)";

        logger.debug("Attempting to save reset token for user ID: {}", dto.getUserId());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getUserId());
            stmt.setString(2, dto.getSelector());
            stmt.setString(3, dto.getVerifierHash());
            stmt.setTimestamp(4, dto.getExpirationTime());
            stmt.setTimestamp(5, dto.getCreatedAt());

            stmt.executeUpdate();
            logger.info("Successfully saved reset token for user ID: {}", dto.getUserId());
        } catch (SQLException e) {
            logger.error("Error saving reset token for user ID: {}", dto.getUserId(), e);
            throw e;
        }
    }

    /**
     * Trova un token usando il selettore (veloce e sicuro).
     * Questa query è performante perché la colonna 'selector' è indicizzata.
     * @param selector La parte pubblica del token.
     * @param conn La connessione al database.
     * @return Il PasswordResetDTO se viene trovato un token valido, altrimenti null.
     * @throws SQLException in caso di errore DB.
     */
    public PasswordResetDTO findBySelector(String selector, Connection conn) throws SQLException {
        // La query può già escludere i token scaduti per maggiore efficienza
        String sql = "SELECT token_id, user_id, selector, verifier_hash, expiration_time, created_at " +
                     "FROM password_reset_tokens WHERE selector = ? AND expiration_time > NOW()";

        logger.debug("Attempting to retrieve reset token by selector: {}", selector);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selector);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PasswordResetDTO dto = new PasswordResetDTO();
                dto.setTokenId(rs.getInt("token_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setSelector(rs.getString("selector"));
                dto.setVerifierHash(rs.getString("verifier_hash"));
                dto.setExpirationTime(rs.getTimestamp("expiration_time"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));

                logger.debug("Found valid reset token for user ID: {}", dto.getUserId());
                return dto;
            }
            logger.debug("No valid reset token found for selector: {}", selector);
            return null;
        } catch (SQLException e) {
            logger.error("Error retrieving reset token by selector: {}", selector, e);
            throw e;
        }
    }

    /**
     * Elimina un token usando il suo selettore dopo che è stato usato.
     * @param selector La parte pubblica del token da eliminare.
     * @param conn La connessione al database.
     * @throws SQLException in caso di errore DB.
     */
    public void deleteBySelector(String selector, Connection conn) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE selector = ?";

        logger.debug("Attempting to delete reset token by selector: {}", selector);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selector);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Successfully deleted used reset token with selector: {}", selector);
            } else {
                logger.warn("No reset token found to delete for selector: {}", selector);
            }
        } catch (SQLException e) {
            logger.error("Error deleting reset token by selector: {}", selector, e);
            throw e;
        }
    }

    /**
     * METODO MANTENUTO: Utile per la pulizia periodica del database.
     * Non è critico per il flusso di reset password, ma è buona manutenzione.
     * @param conn La connessione al database.
     * @return il numero di righe eliminate.
     * @throws SQLException in caso di errore DB.
     */
    public int deleteExpiredTokens(Connection conn) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE expiration_time < NOW()";

        logger.debug("Attempting to delete all expired password reset tokens");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted {} expired password reset tokens", affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            logger.error("Error deleting expired password reset tokens.", e);
            throw e;
        }
    }

    // --- METODI OBSOLETI RIMOSSI ---
    // public void insertResetToken(...) -> rimpiazzato da save(...)
    // public PasswordResetDTO getByTokenHash(...) -> rimpiazzato da findBySelector(...)
    // public void deleteToken(...) -> rimpiazzato da deleteBySelector(...)
    // public List<PasswordResetDTO> getAllActiveResets(...) -> Rimosso perché inefficiente e non più necessario
}
