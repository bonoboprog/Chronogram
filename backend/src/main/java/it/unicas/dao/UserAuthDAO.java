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
import java.time.format.DateTimeFormatter;

public class UserAuthDAO {

    private static final Logger logger = LogManager.getLogger(UserAuthDAO.class);

    public boolean insertUserAuth(UserAuthDTO userAuth) {
        String SQL = "INSERT INTO user_auth(user_id, email, password_hash, created_at, last_login, username) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);

            // Get current timestamp for created_at
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            pstmt.setInt(1, userAuth.getUserId());
            pstmt.setString(2, userAuth.getEmail());
            pstmt.setString(3, userAuth.getPasswordHash());
            pstmt.setString(4, formattedDateTime);
            pstmt.setTimestamp(5, userAuth.getLastLogin());
            pstmt.setString(6, userAuth.getUsername());

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
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing PreparedStatement in insertUserAuth", ex);
            }
        }
        return success;
    }

    public String getPasswordHashByEmail(String email) {
        String SQL = "SELECT password_hash FROM user_auth WHERE email = ?";
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
                passwordHash = rs.getString("password_hash");
                logger.debug("Retrieved password hash for email: {}", email);
            } else {
                logger.warn("No user found with email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving password hash for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing resources in getPasswordHashByEmail", ex);
            }
        }
        return passwordHash;
    }

    public UserAuthDTO getUserAuthByEmail(String email) {
        String SQL = "SELECT user_id, email, password_hash, created_at, last_login, username " +
                "FROM user_auth WHERE email = ?";
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
                        rs.getString("password_hash"),
                        rs.getString("created_at"),
                        rs.getTimestamp("last_login"),
                        rs.getString("username")
                );
                logger.debug("Retrieved UserAuth for email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving UserAuth for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing resources in getUserAuthByEmail", ex);
            }
        }
        return userAuth;
    }

    public boolean updateLastLogin(String email) {
        String SQL = "UPDATE user_auth SET last_login = ? WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(SQL);
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
                logger.info("Updated last_login for email: {}", email);
            }
        } catch (SQLException e) {
            logger.error("Error updating last_login for email: " + email, e);
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                logger.error("Error closing PreparedStatement in updateLastLogin", ex);
            }
        }
        return success;
    }
}