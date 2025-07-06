package it.unicas.service.activity;

import it.unicas.dao.ActivityTypeDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.ActivityTypeDTO;
import it.unicas.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for retrieving a list of all available activity types.
 */
public class ListActivityTypesService {
    private static final Logger logger = LogManager.getLogger(ListActivityTypesService.class);

    private final ActivityTypeDAO activityTypeDAO;

    public ListActivityTypesService() {
        this.activityTypeDAO = new ActivityTypeDAO();
    }

    /**
     * Retrieves all activity types from the database.
     * @return A list of all ActivityTypeDTOs.
     * @throws ServiceException If a database error occurs during retrieval.
     */
    public List<ActivityTypeDTO> getAllActivityTypes() throws ServiceException {
        logger.info("Retrieving all activity types.");
        try (Connection conn = DBUtil.getConnection()) {
            List<ActivityTypeDTO> activityTypes = activityTypeDAO.getAllActivityTypes(conn);
            logger.info("Successfully retrieved {} activity types.", activityTypes.size());
            return activityTypes;
        } catch (SQLException e) {
            logger.error("Database error while retrieving all activity types.", e);
            throw new ServiceException("Failed to retrieve activity types due to database error.", e);
        }
    }
}