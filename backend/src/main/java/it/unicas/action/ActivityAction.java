package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.ActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ActivityAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(ActivityAction.class);
    private final ActivityService activityService = new ActivityService();

    // --- INPUT PARAMETERS ---
    private Integer activityId;
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer userId;
    private Integer activityTypeId;

    // --- OUTPUT WRAPPER ---
    private ActivityResponse activityResponse;

    public String create() {
        logger.info("Creating new activity for user_id: {}", userId);
        try {
            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO createdActivity = activityService.createActivity(activityData);
            setSuccess("Activity created successfully", createdActivity);
            logger.info("Activity created successfully with ID: {}", createdActivity.getActivityId());
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to create activity");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred");
        }
        return SUCCESS;
    }

    public String update() {
        logger.info("Updating activity ID: {}", activityId);
        try {
            if (activityId == null) {
                throw new ValidationException("Activity ID is required");
            }
            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO updatedActivity = activityService.updateActivity(activityId, activityData);
            setSuccess("Activity updated successfully", updatedActivity);
            logger.info("Activity updated successfully");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to update activity");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred");
        }
        return SUCCESS;
    }

    public String delete() {
        logger.info("Deleting activity ID: {}", activityId);
        try {
            if (activityId == null || userId == null) {
                throw new ValidationException("Activity ID and User ID are required");
            }
            activityService.deleteActivity(activityId, userId);
            setSuccess("Activity deleted successfully", null);
            logger.info("Activity deleted successfully");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to delete activity");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred");
        }
        return SUCCESS;
    }

    public String list() {
        logger.info("Retrieving today's activities for user: {}", userId);
        try {
            if (userId == null) {
                throw new ValidationException("User ID is required");
            }
            List<ActivityDTO> activities = activityService.getTodaysActivities(userId);
            setSuccessList("Today's activities retrieved", activities);
            logger.info("Retrieved {} activities", activities.size());
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error", e);
            setFailure("Failed to retrieve activities");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            setFailure("An unexpected error occurred");
        }
        return SUCCESS;
    }

    private ActivityDTO buildActivityDTOFromInput() {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId);
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);
        return activity;
    }

    private void setSuccess(String message, ActivityDTO activity) {
        this.activityResponse = new ActivityResponse(true, message, activity, null);
    }

    private void setSuccessList(String message, List<ActivityDTO> activities) {
        this.activityResponse = new ActivityResponse(true, message, null, activities);
    }

    private void setFailure(String message) {
        this.activityResponse = new ActivityResponse(false, message, null, null);
    }

    // Simplified response DTO
    public static class ActivityResponse {
        private boolean success;
        private String message;
        private ActivityDTO activity;
        private List<ActivityDTO> activities;

        public ActivityResponse(boolean success, String message,
                                ActivityDTO activity, List<ActivityDTO> activities) {
            this.success = success;
            this.message = message;
            this.activity = activity;
            this.activities = activities;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public ActivityDTO getActivity() { return activity; }
        public List<ActivityDTO> getActivities() { return activities; }
    }

    // Getters and setters
    public ActivityResponse getActivityResponse() { return activityResponse; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}