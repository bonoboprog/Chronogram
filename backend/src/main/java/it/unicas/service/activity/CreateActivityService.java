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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service for creating a new activity.
 * Encapsulates business logic and transaction management for activity creation.
 */
public class CreateActivityService {
    private static final Logger logger = LogManager.getLogger(CreateActivityService.class);

    private final ActivityDAO activityDAO;
    private final ActivityTypeDAO activityTypeDAO;

    public CreateActivityService() {
        this.activityDAO = new ActivityDAO();
        this.activityTypeDAO = new ActivityTypeDAO();
    }

    /**
     * Creates a new activity.
     * @param activityData The ActivityDTO containing the data for the new activity.
     * @return The created ActivityDTO with its generated ID and joined activity type details.
     * @throws ValidationException If the provided activity data is invalid.
     * @throws ServiceException If a database error occurs or activity creation fails.
     */
    public ActivityDTO createActivity(ActivityDTO activityData) throws ValidationException, ServiceException {
        logger.info("Processing creation of new activity for user_id: {}", activityData.getUserId());
        validateActivityData(activityData);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Set activity date to today if not provided (or validate if provided)
            if (activityData.getActivityDate() == null) {
                activityData.setActivityDate(Date.valueOf(LocalDate.now()));
            }

            // Verify that the activity type exists
            if (!activityTypeDAO.activityTypeExists(activityData.getActivityTypeId(), conn)) {
                throw new ValidationException("Invalid activity type ID: " + activityData.getActivityTypeId());
            }

            // Set timestamps
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            activityData.setCreatedAt(now);
            activityData.setUpdatedAt(now);

            // Insert the activity
            int activityId = activityDAO.insertActivity(activityData, conn);
            if (activityId == -1) {
                throw new ServiceException("Failed to insert activity into database.");
            }

            // Retrieve the complete activity with type information for the response
            ActivityDTO createdActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);
            conn.commit(); // Commit transaction
            logger.info("Activity created successfully with ID: {}", activityId);
            return createdActivity;
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error during activity creation for user_id: {}", activityData.getUserId(), e);
            throw new ServiceException("Failed to create activity due to database error.", e);
        } catch (ValidationException | ServiceException e) {
            rollback(conn); // Rollback if a business/validation exception occurs after transaction start
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            rollback(conn);
            logger.error("Unexpected error during activity creation for user_id: {}", activityData.getUserId(), e);
            throw new ServiceException("An unexpected system error occurred during activity creation.", e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Validates the data for a new activity.
     * @param activity The ActivityDTO to validate.
     * @throws ValidationException If any validation rule is violated.
     */
    private void validateActivityData(ActivityDTO activity) throws ValidationException {
        if (activity.getUserId() == null) {
            throw new ValidationException("User ID is required.");
        }
        if (activity.getActivityTypeId() == null) {
            throw new ValidationException("Activity type ID is required.");
        }
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