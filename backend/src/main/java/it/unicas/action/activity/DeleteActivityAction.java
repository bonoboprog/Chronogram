package it.unicas.action.activity;

//import it.unicas.action.BaseAction;
import it.unicas.service.activity.DeleteActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;

/**
 * Struts2 Action for deleting an activity.
 * Handles input parameters and delegates to DeleteActivityService.
 */
public class DeleteActivityAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer activityId;
    private Integer userId; // Required for ownership check

    // Service dependency
    private final DeleteActivityService deleteActivityService = new DeleteActivityService();

    @Override
    public String execute() {
        logger.info("Attempting to delete activity ID: {} for user_id: {}", activityId, userId);
        try {
            if (activityId == null || userId == null) {
                throw new ValidationException("Activity ID and User ID are required for deletion.");
            }
            deleteActivityService.deleteActivity(activityId, userId);
            setSuccess("Activity deleted successfully");
            logger.info("Activity ID: {} deleted successfully.", activityId);
        } catch (ValidationException e) {
            logger.warn("Validation error during activity deletion: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error during activity deletion", e);
            setFailure("Failed to delete activity due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity deletion", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    // --- Getters and setters for Struts2 input binding ---
    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
