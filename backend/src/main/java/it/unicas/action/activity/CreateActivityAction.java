package it.unicas.action.activity;

//import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.CreateActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpServletRequest;

public class CreateActivityAction extends BaseAction {
    // --- INPUT PARAMETERS (REMOVE userId) ---
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer activityTypeId; // Only frontend-settable inputs remain

    private final CreateActivityService createActivityService = new CreateActivityService();

    @Override
    public String execute() {
        // Retrieve authenticated user ID from request (added)
        HttpServletRequest request = ServletActionContext.getRequest();
        Integer userId = (Integer) request.getAttribute("authenticatedUserId");

        if (userId == null) {
            logger.warn("Unauthenticated user attempt to create activity");
            setFailure("Utente non autenticato.");
            return SUCCESS; // Or appropriate error code
        }

        logger.info("Attempting to create new activity for user_id: {}", userId);
        try {
            ActivityDTO activityData = buildActivityDTOFromInput(userId); // Pass userId
            ActivityDTO createdActivity = createActivityService.createActivity(activityData);
            setSuccess("Activity created successfully", createdActivity);
            logger.info("Activity created successfully with ID: {}", createdActivity.getActivityId());
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to create activity due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    // Accept userId as parameter (updated)
    private ActivityDTO buildActivityDTOFromInput(Integer userId) {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId); // Use authenticated userId
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);
        return activity;
    }

    // --- Setters (REMOVE setUserId) ---
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}
