package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class UserAuthDAO {

    private static final Logger logger = LogManager.getLogger(UserAuthDAO.class);

    public int insertUserAuth(UserAuthDTO userAuth, Connection conn) throws SQLException {
        final String SQL = "INSERT INTO user_auth(email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, userAuth.getEmail());
            pstmt.setString(2, userAuth.getPasswordHash());
            pstmt.setTimestamp(3, userAuth.getCreatedAt());
            pstmt.setTimestamp(4, userAuth.getUpdatedAt());
            pstmt.setTimestamp(5, userAuth.getLastLogin());
            pstmt.setString(6, userAuth.getUsername());
            pstmt.setInt(7, userAuth.getIsActive());
            pstmt.setInt(8, userAuth.getFailedLoginAttempts());
            pstmt.setTimestamp(9, userAuth.getLockedUntil());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        userAuth.setUserId(userId);
                        logger.info("Inserted UserAuth with generated user_id: {}", userId);
                        return userId;
                    }
                }
            } else {
                logger.warn("No rows affected when inserting UserAuth for email: {}", userAuth.getEmail());
            }

        } catch (SQLException e) {
            logger.error("Error inserting UserAuth for email: {}", userAuth.getEmail(), e);
            throw e; // propagate to calling transaction
        }

        return -1;
    }

    public UserAuthDTO getUserAuthByEmail(String email, Connection conn) throws SQLException {
        final String SQL = "SELECT user_id, email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until " +
                "FROM user_auth WHERE LOWER(email) = LOWER(?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserAuthDTO(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"),
                            rs.getTimestamp("last_login"),
                            rs.getString("username"),
                            rs.getInt("is_active"),
                            rs.getInt("failed_login_attempts"),
                            rs.getTimestamp("locked_until")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving UserAuth for email: {}", email, e);
            throw e;
        }

        return null;
    }

    public boolean updateLastLogin(String email, Connection conn) throws SQLException {
        final String SQL = "UPDATE user_auth SET last_login = ?, updated_at = ?, failed_login_attempts = 0, locked_until = NULL WHERE LOWER(email) = LOWER(?)";

        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setTimestamp(1, now);
            pstmt.setTimestamp(2, now);
            pstmt.setString(3, email);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error updating last_login for email: {}", email, e);
            throw e;
        }
    }

    public boolean updateLoginAttemptsAndLockout(String email, int failedAttempts, Timestamp lockedUntil, Connection conn) throws SQLException {
        final String SQL = "UPDATE user_auth SET failed_login_attempts = ?, locked_until = ?, updated_at = ? WHERE LOWER(email) = LOWER(?)";

        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, failedAttempts);
            pstmt.setTimestamp(2, lockedUntil);
            pstmt.setTimestamp(3, now);
            pstmt.setString(4, email);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error updating login attempts for email: {}", email, e);
            throw e;
        }
    }



    public boolean usernameExists(String username) {
        final String SQL = "SELECT 1 FROM user_auth WHERE LOWER(username) = LOWER(?) LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, username);
            return ps.executeQuery().next(); // true se esiste almeno una riga

        } catch (SQLException e) {
            logger.error("Error checking username existence for '{}'", username, e);
            return true; // fallback prudente: se errore, assume già preso
        }
    }

    public void updatePassword(int userId, String hashedPassword, Connection conn) throws SQLException {
        String sql = "UPDATE user_auth SET password_hash = ?, failed_login_attempts = 0, locked_until = NULL WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }


/**
 * Azzera i tentativi di login falliti e aggiorna la data dell'ultimo login
 * dopo un accesso andato a buon fine.
 * @param userId L'ID dell'utente.
 * @param conn La connessione al database.
 * @throws SQLException
 */
public void resetLoginAttempts(int userId, Connection conn) throws SQLException {
    String sql = "UPDATE user_auth SET failed_login_attempts = 0, last_login_time = NOW(), locked_until = NULL WHERE user_id = ?";
    logger.debug("Resetting login attempts for user_id: {}", userId);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        logger.error("Error resetting login attempts for user_id: {}", userId, e);
        throw e;
    }
}

/**
 * Aggiorna il contatore dei tentativi di login falliti e imposta un eventuale
 * timestamp di blocco per l'account.
 * @param userId L'ID dell'utente.
 * @param attempts il nuovo numero di tentativi falliti.
 * @param lockedUntil il timestamp fino al quale l'account è bloccato (può essere null).
 * @param conn La connessione al database.
 * @throws SQLException
 */
public void updateFailedLoginAttempt(int userId, int attempts, Timestamp lockedUntil, Connection conn) throws SQLException {
    String sql = "UPDATE user_auth SET failed_login_attempts = ?, locked_until = ? WHERE user_id = ?";
    logger.debug("Updating failed login attempts to {} for user_id: {}", attempts, userId);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, attempts);
        stmt.setTimestamp(2, lockedUntil);
        stmt.setInt(3, userId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        logger.error("Error updating failed login attempts for user_id: {}", userId, e);
        throw e;
    }
}

}
