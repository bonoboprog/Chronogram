package it.unicas.action.activity;

import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.UpdateActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpServletRequest;

public class UpdateActivityAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer activityId;  // Still required from frontend
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer activityTypeId;  // Remove userId

    private final UpdateActivityService updateActivityService = new UpdateActivityService();

    @Override
    public String execute() {
        // Retrieve authenticated user ID from request
        HttpServletRequest request = ServletActionContext.getRequest();
        Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");

        if (authenticatedUserId == null) {
            logger.warn("Unauthenticated user attempt to update activity");
            setFailure("Utente non autenticato.");
            return SUCCESS;
        }

        logger.info("Attempting to update activity ID: {} for user_id: {}", activityId, authenticatedUserId);
        try {
            if (activityId == null) {
                throw new ValidationException("Activity ID is required for update.");
            }

            ActivityDTO activityData = buildActivityDTOFromInput(authenticatedUserId);
            ActivityDTO updatedActivity = updateActivityService.updateActivity(activityId, activityData);
            setSuccess("Activity updated successfully", updatedActivity);
            logger.info("Activity ID: {} updated successfully.", activityId);
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to update activity due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    private ActivityDTO buildActivityDTOFromInput(Integer userId) {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId);  // Use authenticated user ID
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);
        return activity;
    }

    // --- Setters (REMOVED setUserId) ---
    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}