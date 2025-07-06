package it.unicas.action.activity;

import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.CreateActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;

/**
 * Struts2 Action for creating a new activity.
 * Handles input parameters and delegates to CreateActivityService.
 */
public class CreateActivityAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer userId;
    private Integer activityTypeId;

    // Service dependency (manual instantiation for now)
    private final CreateActivityService createActivityService = new CreateActivityService();

    @Override
    public String execute() {
        logger.info("Attempting to create new activity for user_id: {}", userId);
        try {
            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO createdActivity = createActivityService.createActivity(activityData);
            setSuccess("Activity created successfully", createdActivity);
            logger.info("Activity created successfully with ID: {}", createdActivity.getActivityId());
        } catch (ValidationException e) {
            logger.warn("Validation error during activity creation: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error during activity creation", e);
            setFailure("Failed to create activity due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity creation", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    /**
     * Builds an ActivityDTO from the action's input parameters.
     * @return A new ActivityDTO instance.
     */
    private ActivityDTO buildActivityDTOFromInput() {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId);
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);
        // activityDate will be set by the service (e.g., to current date)
        return activity;
    }

    // --- Getters and setters for Struts2 input binding ---
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}