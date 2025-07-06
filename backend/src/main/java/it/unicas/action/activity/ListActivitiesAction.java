package it.unicas.action.activity;

import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.ListActivitiesService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;

import java.util.List;

/**
 * Struts2 Action for listing activities.
 * Handles input parameters and delegates to ListActivitiesService.
 */
public class ListActivitiesAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer userId;

    // Service dependency
    private final ListActivitiesService listActivitiesService = new ListActivitiesService();

    @Override
    public String execute() {
        logger.info("Attempting to retrieve today's activities for user: {}", userId);
        try {
            if (userId == null) {
                throw new ValidationException("User ID is required to list activities.");
            }
            List<ActivityDTO> activities = listActivitiesService.getTodaysActivities(userId);
            setSuccess("Today's activities retrieved successfully", activities);
            logger.info("Retrieved {} activities for user: {}", activities.size(), userId);
        } catch (ValidationException e) {
            logger.warn("Validation error during activity listing: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error during activity listing", e);
            setFailure("Failed to retrieve activities due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity listing", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    // --- Getters and setters for Struts2 input binding ---
    public void setUserId(Integer userId) { this.userId = userId; }
}