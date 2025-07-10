package it.unicas.action.activity;

import it.unicas.service.activity.DeleteActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpServletRequest;

public class DeleteActivityAction extends BaseAction {

    // --- INPUT PARAMETERS (REMOVED userId) ---
    private Integer activityId;

    // Service dependency
    private final DeleteActivityService deleteActivityService = new DeleteActivityService();

    @Override
    public String execute() {
        HttpServletRequest request = ServletActionContext.getRequest();
        Integer authenticatedUserId = (Integer) request.getAttribute("authenticatedUserId");

        // Validate authentication
        if (authenticatedUserId == null) {
            logger.warn("Unauthenticated user attempt to delete activity");
            setFailure("Utente non autenticato.");
            return SUCCESS;
        }

        logger.info("Attempting to delete activity ID: {} for user_id: {}", activityId, authenticatedUserId);
        try {
            if (activityId == null) {
                throw new ValidationException("Activity ID is required for deletion.");
            }

            deleteActivityService.deleteActivity(activityId, authenticatedUserId);
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

    // --- Only keep activityId setter ---
    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
}