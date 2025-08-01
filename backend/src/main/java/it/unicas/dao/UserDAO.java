package it.unicas.dao;

import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    public int insertUser(UserDTO user, Connection conn) throws SQLException {
        final String SQL = "INSERT INTO user(" +
                "user_auth_user_id, name, surname, phone, " +
                "weekly_income, weekly_income_other, weekly_home_cost, " +
                "notes, gender, birthday, address, created_at, updated_at) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getSurname());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getWeeklyIncome());
            pstmt.setString(6, user.getWeeklyIncomeOther());
            pstmt.setString(7, user.getWeeklyHomeCost());
            pstmt.setString(8, user.getNotes());
            pstmt.setString(9, user.getGender());
            pstmt.setDate(10, user.getBirthday() != null ? new java.sql.Date(user.getBirthday().getTime()) : null);
            pstmt.setString(11, user.getAddress());
            pstmt.setTimestamp(12, user.getCreatedAt());
            pstmt.setTimestamp(13, user.getUpdatedAt());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("User inserted with user_id (from auth): {}", user.getUserId());
                return user.getUserId();
            } else {
                logger.warn("No rows affected when inserting User.");
            }
        }

        return -1;
    }

    public UserDTO getUserByUserId(int userId) {
        final String SQL = "SELECT " +
                "user_auth_user_id, name, surname, phone, " +
                "weekly_income, weekly_income_other, weekly_home_cost, " +
                "notes, gender, birthday, address, created_at, updated_at " +
                "FROM user WHERE user_auth_user_id = ?";

        UserDTO user = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    user.setUserId(rs.getInt("user_auth_user_id"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setPhone(rs.getString("phone"));
                    user.setWeeklyIncome(rs.getString("weekly_income"));
                    user.setWeeklyIncomeOther(rs.getString("weekly_income_other"));
                    user.setWeeklyHomeCost(rs.getString("weekly_home_cost"));
                    user.setNotes(rs.getString("notes"));
                    user.setGender(rs.getString("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setAddress(rs.getString("address"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by ID: {}", userId, e);
        }

        return user;
    }
}