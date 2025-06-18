package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

public class UserAuthDAO {

    private static final Logger logger = LogManager.getLogger(UserAuthDAO.class);

    private static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public int insertUserAuth(UserAuthDTO userAuth) {
        final String SQL = "INSERT INTO user_auth(email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

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
        }

        return -1;
    }

    public String getPasswordHashByEmail(String email) {
        final String SQL = "SELECT password_hash FROM user_auth WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Retrieved password hash for email: {}", email);
                    return rs.getString("password_hash");
                } else {
                    logger.warn("No user found with email: {}", email);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving password hash for email: {}", email, e);
        }

        return null;
    }

    public UserAuthDTO getUserAuthByEmail(String email) {
        final String SQL = "SELECT user_id, email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until " +
                "FROM user_auth WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Retrieved UserAuth for email: {}", email);
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
        }

        return null;
    }

    public boolean updateLastLogin(String email) {
        final String SQL = "UPDATE user_auth SET last_login = ?, updated_at = ?, failed_login_attempts = 0, locked_until = NULL WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            Timestamp now = now();
            pstmt.setTimestamp(1, now);
            pstmt.setTimestamp(2, now);
            pstmt.setString(3, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated last_login and reset attempts for email: {}", email);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating last_login for email: {}", email, e);
        }

        return false;
    }

    public boolean updateLoginAttemptsAndLockout(String email, int failedAttempts, Timestamp lockedUntil) {
        final String SQL = "UPDATE user_auth SET failed_login_attempts = ?, locked_until = ?, updated_at = ? WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, failedAttempts);
            pstmt.setTimestamp(2, lockedUntil);
            pstmt.setTimestamp(3, now());
            pstmt.setString(4, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Updated login attempts and lockout for email: {}", email);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating login attempts for email: {}", email, e);
        }

        return false;
    }
}
