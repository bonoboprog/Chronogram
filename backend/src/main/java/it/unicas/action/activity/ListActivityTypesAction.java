package it.unicas.action.activity;

import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityTypeDTO;
import it.unicas.service.activity.ListActivityTypesService;
import it.unicas.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Struts2 Action for listing all available activity types.
 * Delegates to ListActivityTypesService.
 */
public class ListActivityTypesAction extends BaseAction {
    private static final Logger logger = LogManager.getLogger(ListActivityTypesAction.class);

    private final ListActivityTypesService listActivityTypesService = new ListActivityTypesService();

    // Output for Struts2 JSON result
    private List<ActivityTypeDTO> activityTypes;

    @Override
    public String execute() {
        logger.info("Attempting to retrieve all activity types.");
        try {
            activityTypes = listActivityTypesService.getAllActivityTypes();
            setSuccess("Activity types retrieved successfully", activityTypes);
            logger.info("Successfully retrieved {} activity types.", activityTypes.size());
        } catch (ServiceException e) {
            logger.error("Service error during activity type listing", e);
            setFailure("Failed to retrieve activity types due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity type listing", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    // Getter for Struts2 JSON result
    public List<ActivityTypeDTO> getActivityTypes() {
        return activityTypes;
    }
}