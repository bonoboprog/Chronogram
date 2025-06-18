package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Import for Timestamp
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    public int insertUser(UserDTO user) {
        String SQL = "INSERT INTO user(user_auth_user_id, weekly_income, weekly_income_other, weekly_home_cost, notes, gender, birthday, address, created_at, updated_at) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        int userId = -1;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, user.getUserId()); // Manually set from UserAuth
            pstmt.setString(2, user.getWeeklyIncome());
            pstmt.setString(3, user.getWeeklyIncomeOther());
            pstmt.setString(4, user.getWeeklyHomeCost());
            pstmt.setString(5, user.getNotes());
            pstmt.setString(6, user.getGender());
            pstmt.setDate(7, user.getBirthday() != null ? new java.sql.Date(user.getBirthday().getTime()) : null);
            pstmt.setString(8, user.getAddress());
            pstmt.setTimestamp(9, user.getCreatedAt());
            pstmt.setTimestamp(10, user.getUpdatedAt());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                userId = user.getUserId(); // Since it's passed in manually
                logger.info("User inserted with user_id (from auth): {}", userId);
            } else {
                logger.warn("No rows affected when inserting User.");
            }

        } catch (SQLException e) {
            logger.error("Error inserting User with user_id: " + user.getUserId(), e);
        }

        return userId;
    }


    public UserDTO getUserByUserId(int userId) {
        // Updated SQL to select created_at and updated_at
        String SQL = "SELECT user_auth_user_id, weekly_income, weekly_income_other, weekly_home_cost, notes, gender, birthday, address, created_at, updated_at FROM user WHERE user_auth_user_id =?";
        UserDTO user = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    user.setUserId(rs.getInt("user_auth_user_id"));
                    user.setWeeklyIncome(rs.getString("weekly_income"));
                    user.setWeeklyIncomeOther(rs.getString("weekly_income_other"));
                    user.setWeeklyHomeCost(rs.getString("weekly_home_cost"));
                    user.setNotes(rs.getString("notes"));
                    user.setGender(rs.getString("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setAddress(rs.getString("address"));
                    user.setCreatedAt(rs.getTimestamp("created_at")); // New field
                    user.setUpdatedAt(rs.getTimestamp("updated_at")); // New field
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by ID: " + userId, e);
        }
        return user;
    }
}
