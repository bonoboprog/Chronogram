package it.unicas.dbutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for obtaining and closing a JDBC connection to MySQL.
 * Connection parameters are read from environment variables:
 *   DB_NAME, DB_USER, DB_PASS
 * <p>
 * NOTE: Move VM_IP to an external configuration (.env or similar)
 *       if the database host can change across environments.
 */
public class DBUtil {

    private static final Logger logger = LogManager.getLogger(DBUtil.class);

    // TODO: move to configuration
    private static final String VM_IP = "192.168.1.65";

    private static final String JDBC_URL_TEMPLATE =
            "jdbc:mysql://%s:3306/%s?useSSL=false&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.fatal("MySQL JDBC driver not found in classpath", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Establishes a connection using environment variables.
     *
     * @return an open {@link Connection}
     */
    public static Connection getConnection() {
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");

        if (dbName == null || dbUser == null || dbPass == null) {
            logger.error("Missing environment variables for DB connection:");
            if (dbName == null) logger.error(" - DB_NAME is missing");
            if (dbUser == null) logger.error(" - DB_USER is missing");
            if (dbPass == null) logger.error(" - DB_PASS is missing");
            throw new IllegalStateException("Incomplete database configuration");
        }

        String jdbcUrl = String.format(JDBC_URL_TEMPLATE, VM_IP, dbName);
        logger.debug("Connecting with JDBC URL: {}", jdbcUrl);

        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
            logger.info("Database connection established");
            return conn;
        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to connect to database", e);
        }
    }

    /**
     * Closes the connection without throwing blocking exceptions.
     *
     * @param conn the {@link Connection} to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    logger.info("Database connection closed");
                }
            } catch (SQLException e) {
                logger.error("Error while closing DB connection: {}", e.getMessage());
            }
        }
    }
}
