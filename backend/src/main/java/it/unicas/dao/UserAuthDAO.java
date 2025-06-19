package it.unicas.dao;

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
            throw e; // propagate to allow rollback
        }

        return -1;
    }

    public UserAuthDTO getUserAuthByEmail(String email) {
        final String SQL = "SELECT user_id, email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until " +
                           "FROM user_auth WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = it.unicas.dbutil.DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

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
        }

        return null;
    }
}
