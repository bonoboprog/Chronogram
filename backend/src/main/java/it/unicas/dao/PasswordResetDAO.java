package it.unicas.dao;

import it.unicas.dto.PasswordResetDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class PasswordResetDAO {

    private static final Logger logger = LogManager.getLogger(PasswordResetDAO.class);

    public void insertResetToken(PasswordResetDTO dto, Connection conn) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, token_hash, expiration_time, created_at)" +
                "VALUES (?, ?, ?, ?)" +
                "ON DUPLICATE KEY UPDATE token_hash = VALUES(token_hash), expiration_time = VALUES(expiration_time), created_at = VALUES(created_at)";

        logger.debug("Attempting to insert/update reset token for user ID: {}", dto.getUserId());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getUserId());
            stmt.setString(2, dto.getToken_hash());
            stmt.setTimestamp(3, dto.getExpirationTime());
            stmt.setTimestamp(4, dto.getCreated_at());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Successfully {} reset token for user ID: {}",
                        affectedRows == 1 ? "inserted" : "updated", dto.getUserId());
            } else {
                logger.warn("No rows affected when inserting/updating reset token for user ID: {}", dto.getUserId());
            }
        } catch (SQLException e) {
            logger.error("Error inserting/updating reset token for user ID: {}. Error: {}",
                    dto.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    public PasswordResetDTO getByTokenHash(String tokenHash, Connection conn) throws SQLException {
        String sql = "SELECT user_id, token_hash, expiration_time, created_at FROM password_reset_tokens WHERE token_hash = ?";

        logger.debug("Attempting to retrieve reset token by hash (hash truncated in logs): {}",
                tokenHash != null && tokenHash.length() > 8 ?
                        tokenHash.substring(0, 4) + "..." + tokenHash.substring(tokenHash.length()-4) : "[null or short hash]");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PasswordResetDTO dto = new PasswordResetDTO();
                dto.setUserId(rs.getInt("user_id"));
                dto.setToken_hash(rs.getString("token_hash"));
                dto.setExpirationTime(rs.getTimestamp("expiration_time"));
                dto.setCreated_at(rs.getTimestamp("created_at"));

                logger.debug("Found reset token for user ID: {}", dto.getUserId());
                return dto;
            }
            logger.debug("No reset token found for the provided hash");
            return null;
        } catch (SQLException e) {
            logger.error("Error retrieving reset token by hash. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteToken(String tokenHash, Connection conn) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE token_hash = ?";

        logger.debug("Attempting to delete reset token (hash truncated in logs): {}",
                tokenHash != null && tokenHash.length() > 8 ?
                        tokenHash.substring(0, 4) + "..." + tokenHash.substring(tokenHash.length()-4) : "[null or short hash]");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Successfully deleted reset token (affected rows: {})", affectedRows);
            } else {
                logger.debug("No reset token found to delete for the provided hash");
            }
        } catch (SQLException e) {
            logger.error("Error deleting reset token. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<PasswordResetDTO> getAllActiveResets(Connection conn) throws SQLException {
        String sql = "SELECT user_id, token_hash, expiration_time, created_at FROM password_reset_tokens";
        List<PasswordResetDTO> list = new ArrayList<>();

        logger.debug("Retrieving all active password reset tokens");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PasswordResetDTO dto = new PasswordResetDTO();
                dto.setUserId(rs.getInt("user_id"));
                dto.setToken_hash(rs.getString("token_hash"));
                dto.setExpirationTime(rs.getTimestamp("expiration_time"));
                dto.setCreated_at(rs.getTimestamp("created_at"));
                list.add(dto);
            }
            logger.debug("Found {} active password reset tokens", list.size());
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving all active password reset tokens. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public int deleteExpiredTokens(Connection conn) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE expiration_time < NOW()";

        logger.debug("Attempting to delete expired password reset tokens");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted {} expired password reset tokens", affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            logger.error("Error deleting expired password reset tokens. Error: {}", e.getMessage(), e);
            throw e;
        }
    }
}