package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;

public class UserAuthDAO {

    private static final Logger logger = LogManager.getLogger(UserAuthDAO.class);

    public boolean insertUserAuth(UserAuthDTO userAuth) {
        // Updated SQL to include new fields and corrected password_hash
        String SQL = "INSERT INTO user_auth(user_id, email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);

            // Set parameters for the new fields and corrected types
            pstmt.setInt(1, userAuth.getUserId());
            pstmt.setString(2, userAuth.getEmail());
            pstmt.setString(3, userAuth.getPasswordHash()); // Corrected method call
            pstmt.setTimestamp(4, userAuth.getCreatedAt()); // Changed to setTimestamp
            pstmt.setTimestamp(5, userAuth.getUpdatedAt()); // New field
            pstmt.setTimestamp(6, userAuth.getLastLogin());
            pstmt.setString(7, userAuth.getUsername());
            pstmt.setInt(8, userAuth.getIsActive()); // New field
            pstmt.setInt(9, userAuth.getFailedLoginAttempts()); // New field
            pstmt.setTimestamp(10, userAuth.getLockedUntil()); // New field

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                logger.info("UserAuth record inserted for email: {}", userAuth.getEmail());
            } else {
                logger.warn("No rows affected when inserting UserAuth for email: {}", userAuth.getEmail());
            }
        } catch (SQLException e) {
            logger.error("Error inserting UserAuth for email: " + userAuth.getEmail(), e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt!= null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing PreparedStatement in insertUserAuth", ex);
            }
        }
        return success;
    }

    public String getPasswordHashByEmail(String email) {
        // Corrected column name in SQL query
        String SQL = "SELECT password_hash FROM user_auth WHERE email =?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String passwordHash = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                passwordHash = rs.getString("password_hash"); // Corrected column name
                logger.debug("Retrieved password hash for email: {}", email);
            } else {
                logger.warn("No user found with email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving password hash for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (rs!= null) rs.close();
                if (pstmt!= null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing resources in getPasswordHashByEmail", ex);
            }
        }
        return passwordHash;
    }

    public UserAuthDTO getUserAuthByEmail(String email) {
        // Updated SQL to select new fields and corrected password_hash
        String SQL = "SELECT user_id, email, password_hash, created_at, updated_at, last_login, username, is_active, failed_login_attempts, locked_until " +
                "FROM user_auth WHERE email =?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserAuthDTO userAuth = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userAuth = new UserAuthDTO(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("password_hash"), // Corrected column name
                        rs.getTimestamp("created_at"), // Changed to getTimestamp
                        rs.getTimestamp("updated_at"), // New field
                        rs.getTimestamp("last_login"),
                        rs.getString("username"),
                        rs.getInt("is_active"), // New field
                        rs.getInt("failed_login_attempts"), // New field
                        rs.getTimestamp("locked_until") // New field
                );
                logger.debug("Retrieved UserAuth for email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving UserAuth for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (rs!= null) rs.close();
                if (pstmt!= null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing resources in getUserAuthByEmail", ex);
            }
        }
        return userAuth;
    }

    public boolean updateLastLogin(String email) {
        // Updated SQL to also set updated_at, reset failed_login_attempts and locked_until
        String SQL = "UPDATE user_auth SET last_login =?, updated_at =?, failed_login_attempts = 0, locked_until = NULL WHERE email =?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, now); // Set last_login
            pstmt.setTimestamp(2, now); // Set updated_at to current time
            pstmt.setString(3, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                logger.info("Updated last_login, updated_at, and reset login attempts for email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error updating last_login for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt!= null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing PreparedStatement in updateLastLogin", ex);
            }
        }
        return success;
    }

    public boolean updateLoginAttemptsAndLockout(String email, int failedAttempts, Timestamp lockedUntil) {
        String SQL = "UPDATE user_auth SET failed_login_attempts =?, locked_until =?, updated_at =? WHERE email =?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, failedAttempts);
            pstmt.setTimestamp(2, lockedUntil);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Update updated_at
            pstmt.setString(4, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                logger.info("Updated login attempts and lockout status for email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error updating login attempts for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt!= null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing PreparedStatement in updateLoginAttemptsAndLockout", ex);
            }
        }
        return success;
    }
}
