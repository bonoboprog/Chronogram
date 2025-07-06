package it.unicas.service.activity;

import it.unicas.dao.ActivityDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for retrieving a list of activities.
 * Encapsulates business logic for listing activities.
 */
public class ListActivitiesService {
    private static final Logger logger = LogManager.getLogger(ListActivitiesService.class);

    private final ActivityDAO activityDAO;

    public ListActivitiesService() {
        this.activityDAO = new ActivityDAO();
    }

    /**
     * Retrieves all activities for a user for a specific date.
     * @param activityDate The date for which to retrieve activities.
     * @param userId The ID of the user.
     * @return A list of ActivityDTOs for the specified date.
     * @throws ValidationException If the user ID or activity date is null.
     * @throws ServiceException If a database error occurs during retrieval.
     */
    public List<ActivityDTO> getActivitiesByDateAndUser(Date activityDate, Integer userId) throws ValidationException, ServiceException {
        logger.info("Processing retrieval of activities for date: {} and user_id: {}", activityDate, userId);
        if (userId == null) {
            throw new ValidationException("User ID is required to retrieve activities.");
        }
        if (activityDate == null) {
            throw new ValidationException("Activity date is required to retrieve activities.");
        }

        try (Connection conn = DBUtil.getConnection()) { // Connection is closed automatically by try-with-resources
            List<ActivityDTO> activities = activityDAO.getActivitiesByDateAndUser(activityDate, userId, conn);
            logger.info("Retrieved {} activities for date: {} for user_id: {}", activities.size(), activityDate, userId);
            return activities;
        } catch (SQLException e) {
            logger.error("Database error while retrieving activities for date: {} and user_id: {}", activityDate, userId, e);
            throw new ServiceException("Failed to retrieve activities due to database error.", e);
        }
    }
}