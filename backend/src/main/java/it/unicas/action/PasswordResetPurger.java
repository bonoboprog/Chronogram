package it.unicas.action;

import it.unicas.dao.PasswordResetDAO;
import it.unicas.dbutil.DBUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class PasswordResetPurger {

    private static final Logger logger = LogManager.getLogger(PasswordResetPurger.class);

    public static void purgeExpiredTokens() {
        try (Connection conn = DBUtil.getConnection()) {
            PasswordResetDAO dao = new PasswordResetDAO();
            int deletedCount = dao.deleteExpiredTokens(conn);
            logger.info("Purged {} expired password reset tokens.", deletedCount);
        } catch (Exception e) {
            logger.error("Error purging expired password reset tokens", e);
        }
    }
}

// THIS STILL NEED TO BE IMPLEMENTED, THE INTENTION IS TO HAVE A PURGE ON ALL
// THE GENERATED TOKENS TO NOT CLUTTER THE DATABASE
