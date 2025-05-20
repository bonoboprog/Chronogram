package it.unicas.activities.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.mindrot.jbcrypt.BCrypt; // We can't put it for now

import it.unicas.activities.dbutil.DBUtil;
import it.unicas.activities.pojo.LoginInfo;

public class LoginDAO {

    public static boolean isUserValid(LoginInfo userDetails) {
        boolean validStatus = false;

        try {
            Connection conn = DBUtil.getConnection();

            String sql = "SELECT password_hash FROM user_auth WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userDetails.getUserName());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Verifica la password usando BCrypt
                if (BCrypt.checkpw(userDetails.getPassword(), storedHash)) {
                    validStatus = true;
                }
            }

            DBUtil.closeConnection(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return validStatus;
    }
}