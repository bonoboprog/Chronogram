package it.unicas.action.activity;

//import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.UpdateActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;

/**
 * Struts2 Action for updating an existing activity.
 * Handles input parameters and delegates to UpdateActivityService.
 */
public class UpdateActivityAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer activityId;
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer userId; // Required for ownership check
    private Integer activityTypeId;

    // Service dependency
    private final UpdateActivityService updateActivityService = new UpdateActivityService();

    @Override
    public String execute() {
        logger.info("Attempting to update activity ID: {} for user_id: {}", activityId, userId);
        try {
            if (activityId == null) {
                throw new ValidationException("Activity ID is required for update.");
            }
            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO updatedActivity = updateActivityService.updateActivity(activityId, activityData);
            setSuccess("Activity updated successfully", updatedActivity);
            logger.info("Activity ID: {} updated successfully.", activityId);
        } catch (ValidationException e) {
            logger.warn("Validation error during activity update: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error during activity update", e);
            setFailure("Failed to update activity due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity update", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    /**
     * Builds an ActivityDTO from the action's input parameters for update.
     * @return A new ActivityDTO instance with fields to be updated.
     */
    private ActivityDTO buildActivityDTOFromInput() {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId);
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);
        // activityDate is not typically updated via this action, but could be added if needed
        return activity;
    }

    // --- Getters and setters for Struts2 input binding ---
    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}
