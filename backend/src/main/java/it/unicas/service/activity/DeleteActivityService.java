package it.unicas.service.activity;

import it.unicas.dao.ActivityDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service for deleting an activity.
 * Encapsulates business logic and transaction management for activity deletion.
 */
public class DeleteActivityService {
    private static final Logger logger = LogManager.getLogger(DeleteActivityService.class);

    private final ActivityDAO activityDAO;

    public DeleteActivityService() {
        this.activityDAO = new ActivityDAO();
    }

    /**
     * Deletes an activity.
     * @param activityId The ID of the activity to delete.
     * @param userId The ID of the user attempting to delete the activity (for ownership check).
     * @throws ValidationException If activityId or userId are null, or activity not found/access denied.
     * @throws ServiceException If a database error occurs or deletion fails.
     */
    public void deleteActivity(Integer activityId, Integer userId) throws ValidationException, ServiceException {
        logger.info("Processing deletion of activity ID: {} by user_id: {}", activityId, userId);
        if (activityId == null || userId == null) {
            throw new ValidationException("Activity ID and User ID are required for deletion.");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Verify that the activity exists and belongs to the user
            ActivityDTO existingActivity = activityDAO.getActivityByIdAndUser(activityId, userId, conn);
            if (existingActivity == null) {
                throw new ValidationException("Activity not found or access denied.");
            }

            // Perform the deletion
            boolean deleted = activityDAO.deleteActivity(activityId, userId, conn);
            if (!deleted) {
                throw new ServiceException("Failed to delete activity from database.");
            }

            conn.commit(); // Commit transaction
            logger.info("Activity ID: {} deleted successfully.", activityId);
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error during activity deletion for ID: {}", activityId, e);
            throw new ServiceException("Failed to delete activity due to database error.", e);
        } catch (ValidationException | ServiceException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            logger.error("Unexpected error during activity deletion for ID: {}", activityId, e);
            throw new ServiceException("An unexpected system error occurred during activity deletion.", e);
        } finally {
            closeConnection(conn);
        }
    }

    // --- Helper methods for transaction management (can be moved to DBUtil or a TransactionManager) ---
    private void rollback(Connection conn) {
        if (conn!= null) {
            try {
                logger.warn("Transaction is being rolled back.");
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Failed to rollback transaction", ex);
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn!= null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("Failed to close connection", ex);
            }
        }
    }
}