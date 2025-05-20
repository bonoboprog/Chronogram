package it.unicas.activities.dbutil;

import java.sql.*;

public class DBUtil {

    public static Connection getConnection()
    {
        Connection conn = null;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost/chronogram_admin?useSSL=false&serverTimezone=UTC", "chronogram_admin", "diary_user1234");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return conn;
    }


    public static void closeConnection(Connection conn)
    {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
