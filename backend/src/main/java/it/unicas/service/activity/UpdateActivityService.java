package it.unicas.service.activity;

import it.unicas.dao.ActivityDAO;
import it.unicas.dao.ActivityTypeDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Service for updating an existing activity.
 * Encapsulates business logic and transaction management for activity updates.
 */
public class UpdateActivityService {
    private static final Logger logger = LogManager.getLogger(UpdateActivityService.class);

    private final ActivityDAO activityDAO;
    private final ActivityTypeDAO activityTypeDAO;

    public UpdateActivityService() {
        this.activityDAO = new ActivityDAO();
        this.activityTypeDAO = new ActivityTypeDAO();
    }

    /**
     * Updates an existing activity.
     * @param activityId The ID of the activity to update.
     * @param activityData The ActivityDTO containing the updated data.
     * @return The updated ActivityDTO.
     * @throws ValidationException If input data is invalid or activity not found/access denied.
     * @throws ServiceException If a database error occurs or update fails.
     */
    public ActivityDTO updateActivity(Integer activityId, ActivityDTO activityData) throws ValidationException, ServiceException {
        logger.info("Processing update for activity ID: {} by user_id: {}", activityId, activityData.getUserId());
        validateUpdateData(activityData); // Validate fields that are provided for update

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Verify that the activity exists and belongs to the user
            ActivityDTO existingActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);
            if (existingActivity == null) {
                throw new ValidationException("Activity not found or access denied.");
            }

            // If activity type ID is provided, verify it exists
            if (activityData.getActivityTypeId()!= null &&
                    !activityTypeDAO.activityTypeExists(activityData.getActivityTypeId(), conn)) {
                throw new ValidationException("Invalid activity type ID: " + activityData.getActivityTypeId());
            }

            // Merge existing data with provided updates (only update non-null fields from activityData)
            // This is a simple merge; a more robust solution might use reflection or a dedicated mapper.
            if (activityData.getDurationMins()!= null) existingActivity.setDurationMins(activityData.getDurationMins());
            if (activityData.getPleasantness()!= null) existingActivity.setPleasantness(activityData.getPleasantness());
            if (activityData.getLocation()!= null) existingActivity.setLocation(activityData.getLocation());
            if (activityData.getCostEuro()!= null) existingActivity.setCostEuro(activityData.getCostEuro());
            if (activityData.getActivityTypeId()!= null) existingActivity.setActivityTypeId(activityData.getActivityTypeId());
            // activityDate is typically not updated via this action, but could be added if needed

            existingActivity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now())); // Set update timestamp

            // Perform the update
            boolean updated = activityDAO.updateActivity(existingActivity, conn);
            if (!updated) {
                throw new ServiceException("Failed to update activity in database.");
            }

            // Retrieve the updated activity for the response
            ActivityDTO updatedActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);
            conn.commit(); // Commit transaction
            logger.info("Activity ID: {} updated successfully.", activityId);
            return updatedActivity;
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error during activity update for ID: {}", activityId, e);
            throw new ServiceException("Failed to update activity due to database error.", e);
        } catch (ValidationException | ServiceException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            logger.error("Unexpected error during activity update for ID: {}", activityId, e);
            throw new ServiceException("An unexpected system error occurred during activity update.", e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Validates the data provided for an activity update.
     * Only validates fields if they are present (non-null).
     * @param activity The ActivityDTO containing update data.
     * @throws ValidationException If any provided field is invalid.
     */
    private void validateUpdateData(ActivityDTO activity) throws ValidationException {
        if (activity.getUserId() == null) {
            throw new ValidationException("User ID is required for update ownership check.");
        }
        // Only validate if the field is provided for update
        if (activity.getDurationMins()!= null && activity.getDurationMins() < 0) {
            throw new ValidationException("Duration must be non-negative.");
        }
        if (activity.getPleasantness()!= null && (activity.getPleasantness() < -3 || activity.getPleasantness() > 3)) {
            throw new ValidationException("Pleasantness must be between -3 and 3.");
        }
        if (activity.getLocation()!= null && activity.getLocation().length() > 100) {
            throw new ValidationException("Location must not exceed 100 characters.");
        }
        if (activity.getCostEuro()!= null) {
            try {
                double cost = Double.parseDouble(activity.getCostEuro());
                if (cost < 0) {
                    throw new ValidationException("Cost must be non-negative.");
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid cost format.");
            }
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