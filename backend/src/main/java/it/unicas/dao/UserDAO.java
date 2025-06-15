package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    public int insertUser(UserDTO user) {
        String SQL = "INSERT INTO user(weekly_income, weekly_income_other, weekly_home_cost, notes, gender, birthday, address, username, email) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int userId = -1;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getWeeklyIncome());
            pstmt.setString(2, user.getWeeklyIncomeOther());
            pstmt.setString(3, user.getWeeklyHomeCost());
            pstmt.setString(4, user.getNotes());
            pstmt.setString(5, user.getGender());
            pstmt.setDate(6, user.getBirthday() != null ? new java.sql.Date(user.getBirthday().getTime()) : null);
            pstmt.setString(7, user.getAddress());
            pstmt.setString(8, user.getUsername());
            pstmt.setString(9, user.getEmail());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        logger.info("New user inserted with ID: {}", userId);
                    }
                }
            } else {
                logger.warn("No rows affected when inserting new user.");
            }
        } catch (SQLException e) {
            logger.error("Error inserting user: " + user.getEmail(), e);
        }
        return userId;
    }

    public UserDTO getUserByUserId(int userId) {
        String SQL = "SELECT user_id, username, email, weekly_income, weekly_income_other, weekly_home_cost, notes, gender, birthday, address FROM user WHERE user_id = ?";
        UserDTO user = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setWeeklyIncome(rs.getString("weekly_income"));
                    user.setWeeklyIncomeOther(rs.getString("weekly_income_other"));
                    user.setWeeklyHomeCost(rs.getString("weekly_home_cost"));
                    user.setNotes(rs.getString("notes"));
                    user.setGender(rs.getString("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setAddress(rs.getString("address"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by ID: " + userId, e);
        }
        return user;
    }
}