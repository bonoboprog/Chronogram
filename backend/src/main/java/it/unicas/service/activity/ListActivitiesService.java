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
     * Retrieves all activities for a user for the current date.
     * @param userId The ID of the user.
     * @return A list of ActivityDTOs for the current date.
     * @throws ValidationException If the user ID is null.
     * @throws ServiceException If a database error occurs during retrieval.
     */
    public List<ActivityDTO> getTodaysActivities(Integer userId) throws ValidationException, ServiceException {
        logger.info("Processing retrieval of today's activities for user_id: {}", userId);
        if (userId == null) {
            throw new ValidationException("User ID is required to retrieve activities.");
        }

        // Calculate today's date
        Date today = Date.valueOf(LocalDate.now());
        try (Connection conn = DBUtil.getConnection()) { // Connection is closed automatically by try-with-resources
            List<ActivityDTO> activities = activityDAO.getActivitiesByDateAndUser(today, userId, conn);
            logger.info("Retrieved {} activities for today for user_id: {}", activities.size(), userId);
            return activities;
        } catch (SQLException e) {
            logger.error("Database error while retrieving today's activities for user_id: {}", userId, e);
            throw new ServiceException("Failed to retrieve activities due to database error.", e);
        }
    }
}